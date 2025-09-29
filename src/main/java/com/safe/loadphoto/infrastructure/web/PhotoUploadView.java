package com.safe.loadphoto.infrastructure.web;


import com.safe.bike.domain.model.Bike;
import com.safe.bike.domain.model.dto.BikeForPhotoDTO;
import com.safe.bike.infrastructure.persistence.bike.BikeJpaRepository;
import com.safe.loadphoto.domain.model.PhotoExif;
import com.safe.loadphoto.domain.port.in.PhotoExifServicePort;
import com.safe.user.application.service.UserServiceImpl;
import com.safe.user.infrastructure.adapters.input.web.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Route(value = "photo-upload" , layout = MainLayout.class)
@PageTitle("Subir Fotografías de Bicicleta")
public class PhotoUploadView extends VerticalLayout {
    private static final Logger logger = LoggerFactory.getLogger(PhotoUploadView.class);
    private final PhotoExifServicePort photoExifService;

    private Upload upload;
    private MemoryBuffer buffer;
    private Button processButton;
    private Select<BikeForPhotoDTO> bikeSelect;
    private TextField fileNameField;

    // Contenedores para mostrar resultados
    private VerticalLayout previewContainer;
    private VerticalLayout exifContainer;

    // Datos temporales
    private byte[] currentFileData;
    private String currentFileName;

    BikeJpaRepository bikeJpaRepository;
    UserServiceImpl userService;

    public PhotoUploadView(PhotoExifServicePort photoExifService, UserServiceImpl userService, BikeJpaRepository bikeJpaRepository) {
        this.photoExifService = photoExifService;
        this.bikeJpaRepository = bikeJpaRepository;
        this.userService  = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

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

        Paragraph description = new Paragraph(
                "Selecciona una bicicleta y sube fotografías. El sistema extraerá automáticamente " +
                        "los datos EXIF incluyendo ubicación GPS, modelo de cámara y fecha de captura."
        );
        description.getStyle().set("color", "var(--lumo-secondary-text-color)");
        add(description);
    }

    @Transactional(readOnly = true) // ✅ Mantener la sesión abierta
    private void createBikeSelector() {
        bikeSelect = new Select<>();
        bikeSelect.setLabel("🚴 Seleccionar Bicicleta");
        bikeSelect.setPlaceholder("Elige la bicicleta para asociar las fotos");
        bikeSelect.setWidthFull();

        // ✅ USAR LA SESIÓN DE VAADIN en lugar de Spring Security Context
        String userEmail = (String) VaadinSession.getCurrent().getAttribute("userEmail");
        String authToken = (String) VaadinSession.getCurrent().getAttribute("authToken");

        logger.info("Email desde sesión Vaadin: {}", userEmail);
        logger.info("Token presente: {}", authToken != null ? "Sí" : "No");

        try {
            // Obtener el ID del usuario actual usando el método helper
            Long userId = userService.getCurrentUserId();

            if (userId != null) {

                List<BikeForPhotoDTO> userBikes = bikeJpaRepository.findSummariesByUserId(userId);

                if (userBikes.isEmpty()) {
                    //bikeSelect.setItems("No tienes bicicletas registradas");
                    bikeSelect.setEnabled(false);
                    Notification.show("ℹ️ Primero debes registrar una bicicleta para poder subir fotos.");
                } else {
                    // Opción 1: Mostrar texto, guardar BikeSummaryDTO
                    bikeSelect.setItems(userBikes);
                    bikeSelect.setItemLabelGenerator(BikeForPhotoDTO::getDisplayLabel);
                }
            } else {
                Notification.show("❌ No hay sesión activa. Por favor, inicia sesión.");
                getUI().ifPresent(ui -> ui.navigate("login"));
            }
        } catch (Exception e) {
            logger.error("Error al cargar las bicicletas del usuario", e);
            Notification.show("❌ Error al cargar las bicicletas: " + e.getMessage());
        }

        add(bikeSelect);
    }

    private void createUploadArea() {
        buffer = new MemoryBuffer();
        upload = new Upload(buffer);

        upload.setAcceptedFileTypes(
                "image/jpeg",
                "image/jpg",
                "image/png",
                ".heic",
                ".heif",
                ".HEIC",
                ".HEIF"
        );
        upload.setMaxFiles(1);
        upload.setMaxFileSize(50 * 1024 * 1024); // 50MB - debe coincidir con la config

        upload.setUploadButton(new Button("📁 Seleccionar Imagen"));
        upload.setDropLabel(new Paragraph("Arrastra tu imagen aquí o (máximo 50MB)"));

        upload.addSucceededListener(event -> {
            currentFileName = event.getFileName();
            try {
                currentFileData = buffer.getInputStream().readAllBytes();
                fileNameField.setValue(currentFileName);
                showPreview();
                processButton.setEnabled(bikeSelect.getValue() != null);

                // Mostrar tamaño del archivo
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

            // Personalizar mensajes de error
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

    // Método helper para formatear tamaño de archivo
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

        // Habilitar solo cuando hay archivo y bicicleta seleccionada
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

            BikeForPhotoDTO selectedBike = bikeSelect.getValue();
            Long bikeId = selectedBike.getBikeId();

            PhotoExif photoExif = photoExifService.extractAndSaveExif(
                    "uploaded", currentFileName, currentFileData, bikeId);

            if (photoExif != null) {
                showExifData(photoExif);
                Notification.show("🎉 ¡Imagen procesada y guardada exitosamente!")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // Resetear formulario
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

        // Información de la cámara
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

        // Fecha y hora
        if (exif.getDateTime() != null) {
            exifContainer.add(createInfoField("📅 Fecha/Hora:", exif.getDateTime()));
        }

        // Ubicación GPS
        if (exif.getLatitude() != null && exif.getLongitude() != null) {
            HorizontalLayout gpsLayout = new HorizontalLayout();
            gpsLayout.add(
                    createInfoField("📍 Latitud:", String.format("%.6f", exif.getLatitude())),
                    createInfoField("📍 Longitud:", String.format("%.6f", exif.getLongitude()))
            );
            exifContainer.add(gpsLayout);

            // TODO: Aquí podrías agregar un mapa pequeño o enlace a Google Maps
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

        // Resetear el componente upload
        upload.clearFileList();
    }


}