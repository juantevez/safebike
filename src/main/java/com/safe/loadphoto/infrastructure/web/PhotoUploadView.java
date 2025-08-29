package com.safe.loadphoto.infrastructure.web;

import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.bike.domain.port.in.BikeServicePort;
import com.safe.loadphoto.domain.model.PhotoExif;
import com.safe.loadphoto.domain.port.in.PhotoExifServicePort;
import com.safe.user.domain.model.User;
import com.safe.user.infrastructure.adapters.input.security.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Route("photo-upload")
@PageTitle("Subir Fotografías de Bicicleta")
public class PhotoUploadView extends VerticalLayout implements BeforeEnterObserver {

    private final PhotoExifServicePort photoExifService;
    private final BikeServicePort bikeService; // Servicio para obtener bicicletas
    private final SecurityService securityService; // Servicio de seguridad para obtener usuario actual

    private Upload upload;
    private MemoryBuffer buffer;
    private Button processButton;
    private Select<BikeEntity> bikeSelect; // Cambiar a Select<Bike> en lugar de String
    private TextField fileNameField;

    // Contenedores para mostrar resultados
    private VerticalLayout previewContainer;
    private VerticalLayout exifContainer;

    // Datos temporales
    private byte[] currentFileData;
    private String currentFileName;
    private User currentUser; // Usuario actual

    public PhotoUploadView(PhotoExifServicePort photoExifService,
                           BikeServicePort bikeService,
                           SecurityService securityService) {
        this.photoExifService = photoExifService;
        this.bikeService = bikeService;
        this.securityService = securityService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Obtener el usuario actual
        currentUser = securityService.getAuthenticatedUser();

        if (currentUser == null) {
            // Redirigir al login si no hay usuario autenticado
            event.forwardTo("login");
            return;
        }

        // Inicializar la vista después de confirmar que hay usuario
        initializeView();
        loadUserBikes();
    }

    private void initializeView() {
        createHeader();
        createBikeSelector();
        createUploadArea();
        createProcessButton();
        createPreviewArea();
        createExifDisplayArea();
    }

    private void createHeader() {
        H2 title = new H2("📸 Subir Fotografías de Bicicleta");
        title.getStyle().set("margin-bottom", "20px");
        add(title);

        // Mostrar usuario actual
        Span userInfo = new Span("👤 Usuario: " + currentUser.getUsername());
        userInfo.getStyle().set("color", "var(--lumo-primary-text-color)")
                .set("font-weight", "500")
                .set("margin-bottom", "10px");
        add(userInfo);

        Paragraph description = new Paragraph(
                "Selecciona una bicicleta y sube fotografías. El sistema extraerá automáticamente " +
                        "los datos EXIF incluyendo ubicación GPS, modelo de cámara y fecha de captura."
        );
        description.getStyle().set("color", "var(--lumo-secondary-text-color)");
        add(description);
    }

 //  private void createBikeSelector() {
 //      bikeSelect = new Select<>();
 //      bikeSelect.setLabel("🚴 Seleccionar Bicicleta");
 //      bikeSelect.setPlaceholder("Elige la bicicleta para asociar las fotos");
 //      bikeSelect.setWidthFull();

 //      // Configurar cómo mostrar las bicicletas en el selector
 //      bikeSelect.setItemLabelGenerator(bike ->
 //              bike.getBrand() + " " + bike.getBikeModel() + " - Serial: " + bike.getSerialNumber());

 //      add(bikeSelect);
 //  }

    private void createBikeSelector() {
        bikeSelect = new Select<>();
        bikeSelect.setLabel("🚴 Seleccionar Bicicleta");
        bikeSelect.setPlaceholder("Elige la bicicleta para asociar las fotos");
        bikeSelect.setWidthFull();

        // Configurar cómo mostrar las bicicletas en el selector
        bikeSelect.setItemLabelGenerator(bikeEntity -> {
            StringBuilder label = new StringBuilder();

            if (bikeEntity.getBrand() != null) {
                label.append(bikeEntity.getBrand().getName());
            }

            if (bikeEntity.getBikeModel() != null) {
                if (label.length() > 0) label.append(" ");
                label.append(bikeEntity.getBikeModel().getModelName());
            }

            if (bikeEntity.getBikeType() != null) {
                label.append(" (").append(bikeEntity.getBikeType().getName()).append(")");
            }

            if (bikeEntity.getSerialNumber() != null && !bikeEntity.getSerialNumber().isEmpty()) {
                label.append(" - Serial: ").append(bikeEntity.getSerialNumber());
            }

            return label.toString();
        });

        add(bikeSelect);
    }

    private void loadUserBikes() {
        try {
            // Cargar las bicicletas del usuario actual directamente como entities
            List<BikeEntity> userBikes = bikeService.getBikesByUserId(currentUser.getId());

            if (userBikes.isEmpty()) {
                // Mostrar mensaje si no hay bicicletas registradas
                Notification notification = Notification.show(
                        "ℹ️ No tienes bicicletas registradas. Registra una bicicleta primero.",
                        5000,
                        Notification.Position.MIDDLE
                );
                notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);

                // Opcional: Agregar botón para ir a registrar bicicleta
                Button registerButton = new Button("➕ Registrar Bicicleta",
                        event -> getUI().ifPresent(ui -> ui.navigate("bike-register")));
                registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                add(registerButton);

                // Deshabilitar el selector
                bikeSelect.setEnabled(false);
            } else {
                // Cargar las bicicletas en el selector
                bikeSelect.setItems(userBikes);
                bikeSelect.setEnabled(true);

                // Mostrar cuántas bicicletas se cargaron
                Notification.show(
                        "✅ Se cargaron " + userBikes.size() + " bicicleta(s) registradas",
                        3000,
                        Notification.Position.BOTTOM_END
                ).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }

        } catch (Exception e) {
            Notification.show("❌ Error al cargar las bicicletas: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            bikeSelect.setEnabled(false);
        }
    }

    private void createUploadArea() {
        buffer = new MemoryBuffer();
        upload = new Upload(buffer);

        upload.setAcceptedFileTypes("image/jpeg", "image/jpg", "image/png");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(50 * 1024 * 1024); // 50MB

        upload.setUploadButton(new Button("📁 Seleccionar Imagen"));
        upload.setDropLabel(new Paragraph("Arrastra tu imagen aquí o (máximo 50MB)"));

        upload.addSucceededListener(event -> {
            currentFileName = event.getFileName();
            try {
                currentFileData = buffer.getInputStream().readAllBytes();
                fileNameField.setValue(currentFileName);
                showPreview();
                processButton.setEnabled(bikeSelect.getValue() != null);

                String fileSize = formatFileSize(currentFileData.length);
                Notification.show("✅ Imagen cargada: " + currentFileName + " (" + fileSize + ")")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            } catch (IOException e) {
                Notification.show("❌ Error al cargar la imagen: " + e.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            if (errorMessage.contains("size")) {
                Notification.show("❌ Archivo demasiado grande. Máximo permitido: 50MB")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (errorMessage.contains("type")) {
                Notification.show("❌ Tipo de archivo no permitido. Solo: JPEG, JPG, PNG")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                Notification.show("❌ Archivo rechazado: " + errorMessage)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        upload.addFailedListener(event -> {
            Notification.show("💥 Error al subir archivo: " + event.getReason().getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        fileNameField = new TextField("📄 Nombre del Archivo");
        fileNameField.setReadOnly(true);
        fileNameField.setWidthFull();

        VerticalLayout uploadSection = new VerticalLayout(upload, fileNameField);
        uploadSection.setSpacing(true);
        uploadSection.setPadding(false);

        add(uploadSection);
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private void createProcessButton() {
        processButton = new Button("🔄 Procesar y Guardar Imagen", event -> processImage());
        processButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        processButton.setEnabled(false);
        processButton.setWidthFull();

        bikeSelect.addValueChangeListener(event ->
                processButton.setEnabled(currentFileData != null && event.getValue() != null));

        add(processButton);
    }

    private void createPreviewArea() {
        previewContainer = new VerticalLayout();
        previewContainer.setVisible(false);
        previewContainer.getStyle().set("border", "2px dashed var(--lumo-contrast-20pct)")
                .set("border-radius", "8px")
                .set("padding", "20px");

        add(previewContainer);
    }

    private void createExifDisplayArea() {
        exifContainer = new VerticalLayout();
        exifContainer.setVisible(false);
        exifContainer.getStyle().set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "8px")
                .set("padding", "20px");

        add(exifContainer);
    }

    private void showPreview() {
        previewContainer.removeAll();

        if (currentFileData != null && currentFileName != null) {
            StreamResource resource = new StreamResource(currentFileName,
                    () -> new ByteArrayInputStream(currentFileData));

            Image preview = new Image(resource, "Vista previa");
            preview.setMaxWidth("300px");
            preview.setMaxHeight("300px");
            preview.getStyle().set("border-radius", "8px")
                    .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");

            H2 previewTitle = new H2("🖼️ Vista Previa");
            previewContainer.add(previewTitle, preview);
            previewContainer.setVisible(true);
        }
    }

    private void processImage() {
        if (currentFileData == null || bikeSelect.getValue() == null) {
            Notification.show("⚠️ Selecciona una bicicleta y carga una imagen")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            processButton.setEnabled(false);
            processButton.setText("⏳ Procesando...");

            BikeEntity selectedBike = bikeSelect.getValue();

            PhotoExif photoExif = photoExifService.extractAndSaveExif(
                    selectedBike.getBikeId().toString(), currentFileName, currentFileData);

            if (photoExif != null) {
                showExifData(photoExif);
                Notification.show("🎉 ¡Imagen procesada y guardada exitosamente para " +
                                selectedBike.getBrand() + " " + selectedBike.getBikeModel() + "!")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                resetForm();
            } else {
                Notification.show("❌ Error al procesar la imagen")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

        } catch (Exception e) {
            Notification.show("💥 Error inesperado: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } finally {
            processButton.setEnabled(true);
            processButton.setText("🔄 Procesar y Guardar Imagen");
        }
    }

    private void showExifData(PhotoExif exif) {
        exifContainer.removeAll();

        H2 exifTitle = new H2("📊 Datos EXIF Extraídos");
        exifContainer.add(exifTitle);

        if (exif.getCameraMaker() != null || exif.getCameraModel() != null) {
            VerticalLayout cameraInfo = new VerticalLayout();
            cameraInfo.setPadding(false);
            cameraInfo.setSpacing(false);

            if (exif.getCameraMaker() != null) {
                cameraInfo.add(createInfoField("🏭 Fabricante:", exif.getCameraMaker()));
            }
            if (exif.getCameraModel() != null) {
                cameraInfo.add(createInfoField("📸 Modelo:", exif.getCameraModel()));
            }

            exifContainer.add(cameraInfo);
        }

        if (exif.getDateTime() != null) {
            exifContainer.add(createInfoField("📅 Fecha/Hora:", exif.getDateTime()));
        }

        if (exif.getLatitude() != null && exif.getLongitude() != null) {
            HorizontalLayout gpsLayout = new HorizontalLayout();
            gpsLayout.add(
                    createInfoField("📍 Latitud:", String.format("%.6f", exif.getLatitude())),
                    createInfoField("📍 Longitud:", String.format("%.6f", exif.getLongitude()))
            );
            exifContainer.add(gpsLayout);

            Button mapButton = new Button("🗺️ Ver en Mapa",
                    event -> openMap(exif.getLatitude(), exif.getLongitude()));
            mapButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            exifContainer.add(mapButton);
        }

        exifContainer.setVisible(true);
    }

    private Div createInfoField(String label, String value) {
        Div container = new Div();
        container.getStyle().set("margin-bottom", "8px");

        Div labelDiv = new Div(label);
        labelDiv.getStyle().set("font-weight", "bold")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "0.875rem");

        Div valueDiv = new Div(value != null ? value : "No disponible");
        valueDiv.getStyle().set("margin-left", "8px");

        HorizontalLayout layout = new HorizontalLayout(labelDiv, valueDiv);
        layout.setSpacing(false);
        layout.setPadding(false);

        container.add(layout);
        return container;
    }

    private void openMap(Double latitude, Double longitude) {
        String mapsUrl = String.format("https://www.google.com/maps?q=%.6f,%.6f",
                latitude, longitude);
        getUI().ifPresent(ui -> ui.getPage().open(mapsUrl, "_blank"));
    }

    private void resetForm() {
        currentFileData = null;
        currentFileName = null;
        fileNameField.clear();
        bikeSelect.clear();
        previewContainer.setVisible(false);
        exifContainer.setVisible(false);
        processButton.setEnabled(false);
        upload.clearFileList();
    }
}