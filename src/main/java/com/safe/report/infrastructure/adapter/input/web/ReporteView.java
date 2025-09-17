package com.safe.report.infrastructure.adapter.input.web;

import com.safe.report.application.service.BikeReportService;
import com.safe.report.domain.model.BikeReportDTO;
import com.safe.user.config.AuthenticationHelper;
import com.safe.user.infrastructure.adapters.input.web.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "reports", layout = MainLayout.class)
@PageTitle("Mis Reportes de Bicicletas")
public class ReporteView extends VerticalLayout implements BeforeEnterObserver {

    private static final Logger log = LoggerFactory.getLogger(ReporteView.class);

    private final BikeReportService bikeReportService;
    private final AuthenticationHelper authHelper;

    private final Button generateButton;
    private final Button previewButton;
    private final Button backButton;
    private final Grid<BikeReportDTO> bikeGrid;
    private final Paragraph summaryParagraph;
    private final Paragraph userInfoParagraph;

    @Autowired
    public ReporteView(BikeReportService bikeReportService, AuthenticationHelper authHelper) {
        log.info("ReporteView inicializado con BikeReportService: {}", bikeReportService);
        this.bikeReportService = bikeReportService;
        this.authHelper = authHelper;

        // Configurar layout principal
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        setPadding(true);
        setSpacing(true);

        // Título principal
        H1 title = new H1("📊 Mis Reportes de Bicicletas");
        title.getStyle().set("color", "#2c3e50");

        // Información del usuario (se llenará en beforeEnter)
        userInfoParagraph = new Paragraph();
        userInfoParagraph.getStyle()
                .set("font-size", "16px")
                .set("color", "#666")
                .set("margin-bottom", "20px")
                .set("text-align", "center");

        // Botones de acción
        generateButton = new Button("📄 Generar PDF", VaadinIcon.FILE_TEXT.create());
        generateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        generateButton.setWidth("180px");
        generateButton.setEnabled(false); // Deshabilitado hasta cargar datos

        previewButton = new Button("👁️ Vista Previa", VaadinIcon.EYE.create());
        previewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        previewButton.setWidth("180px");

        backButton = new Button("🏠 Volver al Menú", VaadinIcon.HOME.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        backButton.setWidth("180px");

        // Configurar listeners de botones
        generateButton.addClickListener(event -> generatePdfReport());
        previewButton.addClickListener(event -> showPreview());
        backButton.addClickListener(event -> navigateToMainMenu());

        HorizontalLayout buttonLayout = new HorizontalLayout(generateButton, previewButton, backButton);
        buttonLayout.setSpacing(true);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Párrafo para mostrar resumen
        summaryParagraph = new Paragraph();
        summaryParagraph.getStyle()
                .set("font-style", "italic")
                .set("color", "#666")
                .set("text-align", "center")
                .set("margin", "20px 0");

        // Grid para mostrar datos
        bikeGrid = createBikeGrid();

        // Agregar componentes al layout
        add(title, userInfoParagraph, buttonLayout, summaryParagraph, bikeGrid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Verificar si el usuario está autenticado
        if (!authHelper.isAuthenticated()) {
            log.warn("Usuario no autenticado intentando acceder a reportes");
            showErrorNotification("❌ Debe iniciar sesión para acceder a los reportes");
            event.rerouteTo("login");
            return;
        }

        // Cargar datos del usuario una vez que se confirma la autenticación
        loadUserData();
    }

    private void loadUserData() {
        try {
            // Obtener email del usuario actual
            String userEmail = authHelper.getCurrentUserEmail()
                    .orElseThrow(() -> new IllegalStateException("No se pudo obtener el email del usuario"));

            log.info("userEmail {}", userEmail);

            // Actualizar información del usuario
            userInfoParagraph.setText("👤 Usuario: " + userEmail);

            // DEBUG: Verificar qué hay en la sesión antes de obtener userId
            VaadinSession session = VaadinSession.getCurrent();
            log.info("=== DEBUG SESIÓN ANTES DE getCurrentUserId ===");
            log.info("Session ID: {}", session != null ? session.getSession().getId() : "NULL");
            if (session != null) {
                log.info("authToken: {}", session.getAttribute("authToken"));
                log.info("userEmail: {}", session.getAttribute("userEmail"));
                log.info("userId: {}", session.getAttribute("userId"));
            }
            log.info("=== FIN DEBUG ===");

            // Obtener ID del usuario
            Long userId = getCurrentUserId();

            if (userId == null) {
                log.warn("No se pudo obtener el ID del usuario para email: {}", userEmail);
                summaryParagraph.setText("⚠️ No se pudo cargar la información del usuario");
                summaryParagraph.getStyle().set("color", "#e74c3c");
                return;
            }

            log.info("Cargando vista previa para usuario: {} (ID: {})", userEmail, userId);

            // Cargar vista previa automáticamente
            loadPreviewData(userId);

        } catch (Exception e) {
            log.error("Error cargando datos del usuario", e);
            showErrorNotification("❌ Error al cargar información del usuario: " + e.getMessage());
        }
    }

    private void loadPreviewData(Long userId) {
        try {
            // Obtener datos y resumen
            List<BikeReportDTO> bikeData = bikeReportService.getBikeDataForUser(userId);
            BikeReportService.ReportSummary summary = bikeReportService.getReportSummary(userId);

            // Actualizar resumen
            if (bikeData.isEmpty()) {
                summaryParagraph.setText("📝 No tienes bicicletas registradas aún. ¡Registra tu primera bicicleta!");
                summaryParagraph.getStyle().set("color", "#f39c12");
                generateButton.setEnabled(false);
            } else {
                summaryParagraph.setText("🚴 " + summary.getSummaryText());
                summaryParagraph.getStyle().set("color", "#27ae60");
                generateButton.setEnabled(true);
            }

            // Mostrar datos en el grid
            bikeGrid.setItems(bikeData);
            bikeGrid.setVisible(!bikeData.isEmpty());

            if (!bikeData.isEmpty()) {
                showSuccessNotification("✅ Se encontraron " + bikeData.size() + " bicicleta(s) registrada(s)");
            }

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación cargando datos: {}", e.getMessage());
            showErrorNotification("⚠️ " + e.getMessage());
            bikeGrid.setVisible(false);
            summaryParagraph.setText("❌ Error cargando datos");
            summaryParagraph.getStyle().set("color", "#e74c3c");
        } catch (Exception e) {
            log.error("Error inesperado cargando vista previa", e);
            showErrorNotification("❌ Error inesperado cargando datos: " + e.getMessage());
        }
    }

    private Grid<BikeReportDTO> createBikeGrid() {
        Grid<BikeReportDTO> grid = new Grid<>(BikeReportDTO.class, false);
        grid.setVisible(false);
        grid.setWidth("100%");
        grid.setMaxHeight("400px");

        // Configurar columnas con iconos
        grid.addColumn(BikeReportDTO::bikeId).setHeader("🆔 ID").setWidth("80px");
        grid.addColumn(BikeReportDTO::brand).setHeader("🏭 Marca").setWidth("120px");
        grid.addColumn(BikeReportDTO::type).setHeader("⚙️ Tipo").setWidth("100px");
        grid.addColumn(BikeReportDTO::model).setHeader("🚲 Modelo").setWidth("150px");
        grid.addColumn(BikeReportDTO::serialNumber).setHeader("📝 Número de Serie").setWidth("180px");
        grid.addColumn(bike -> bike.purchaseDate() != null ?
                        bike.purchaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A")
                .setHeader("📅 Fecha de Compra").setWidth("120px");

        return grid;
    }

    private void generatePdfReport() {
        Long userId = getCurrentUserId();
        log.info("=========================================");
        log.info("Generando PDF para userId: {}", userId);
        log.info("=========================================");

        if (userId == null) {
            showErrorNotification("❌ No se pudo obtener el ID del usuario");
            return;
        }

        // Deshabilitar botón durante la generación
        generateButton.setEnabled(false);
        generateButton.setText("Generando...");

        try {
            // Validar que el usuario tiene bicicletas
            if (!bikeReportService.userHasBikes(userId)) {
                showErrorNotification("❌ No se encontraron bicicletas para generar el reporte");
                return;
            }

            // Generar PDF
            ByteArrayInputStream pdfStream = bikeReportService.generateBikeReportPdf(userId);
            String fileName = bikeReportService.generateFileName(userId);

            // Crear recurso de descarga
            StreamResource resource = new StreamResource(fileName, () -> pdfStream);
            resource.setContentType("application/pdf");
            resource.setCacheTime(0);

            // Crear enlace de descarga
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.setVisible(false);
            add(downloadLink);

            // Simular click para descargar
            downloadLink.getElement().callJsFunction("click");

            showSuccessNotification("🎉 Reporte PDF generado correctamente: " + fileName);

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación generando PDF: {}", e.getMessage());
            showErrorNotification("⚠️ " + e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado generando reporte", e);
            showErrorNotification("❌ Error generando el reporte: " + e.getMessage());
        } finally {
            // Rehabilitar botón
            generateButton.setEnabled(true);
            generateButton.setText("📄 Generar PDF");
        }
    }

    private void showPreview() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            showErrorNotification("❌ No se pudo obtener el ID del usuario");
            return;
        }

        try {
            // Obtener datos y resumen
            List<BikeReportDTO> bikeData = bikeReportService.getBikeDataForUser(userId);
            BikeReportService.ReportSummary summary = bikeReportService.getReportSummary(userId);

            // Actualizar resumen
            summaryParagraph.setText("🚴 " + summary.getSummaryText());

            // Mostrar datos en el grid
            bikeGrid.setItems(bikeData);
            bikeGrid.setVisible(true);

            // Mostrar dialog con vista previa adicional
            showPreviewDialog(summary, bikeData);

            showSuccessNotification("✅ Vista previa actualizada correctamente");

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en vista previa: {}", e.getMessage());
            showErrorNotification("⚠️ " + e.getMessage());
            bikeGrid.setVisible(false);
            summaryParagraph.setText("❌ Error cargando datos");
        } catch (Exception e) {
            log.error("Error inesperado en vista previa", e);
            showErrorNotification("❌ Error cargando vista previa: " + e.getMessage());
        }
    }

    private void showPreviewDialog(BikeReportService.ReportSummary summary, List<BikeReportDTO> bikeData) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("👁️ Vista Previa del Reporte");
        dialog.setWidth("600px");
        dialog.setMaxHeight("500px");

        VerticalLayout dialogContent = new VerticalLayout();
        dialogContent.setPadding(false);
        dialogContent.setSpacing(true);

        // Información del reporte
        H3 userTitle = new H3("👤 Usuario: " + summary.userFullName());
        Paragraph totalInfo = new Paragraph("🚴 Total de bicicletas: " + summary.totalBikes());
        Paragraph dateInfo = new Paragraph("📅 Fecha: " + summary.getFormattedDate());

        dialogContent.add(userTitle, totalInfo, dateInfo);

        // Lista de bicicletas
        if (!bikeData.isEmpty()) {
            H3 bikesTitle = new H3("🚲 Mis Bicicletas:");
            dialogContent.add(bikesTitle);

            for (BikeReportDTO bike : bikeData) {
                Paragraph bikeInfo = new Paragraph(String.format(
                        "• ID: %d - %s %s (%s) - Serie: %s",
                        bike.bikeId(),
                        bike.brand() != null ? bike.brand() : "N/A",
                        bike.model() != null ? bike.model() : "N/A",
                        bike.type() != null ? bike.type() : "N/A",
                        bike.serialNumber() != null ? bike.serialNumber() : "N/A"
                ));
                bikeInfo.getStyle().set("margin", "5px 0");
                dialogContent.add(bikeInfo);
            }
        } else {
            Paragraph noBikes = new Paragraph("📝 No tienes bicicletas registradas aún.");
            noBikes.getStyle().set("color", "#f39c12").set("font-style", "italic");
            dialogContent.add(noBikes);
        }

        dialog.add(dialogContent);

        Button closeButton = new Button("Cerrar", event -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(closeButton);

        dialog.open();
    }

    private void navigateToMainMenu() {
        getUI().ifPresent(ui -> ui.navigate("login")); // Esto llevará al menú principal si está logueado
    }

    /**
     * Obtiene el ID del usuario actual desde la sesión de Vaadin.
     */
    private Long getCurrentUserId() {
        try {
            // Intentar múltiples formas de acceder a la sesión
            VaadinSession session = VaadinSession.getCurrent();
            if (session == null) {
                log.error("VaadinSession.getCurrent() es null");
                return null;
            }

            // Método 1: Usando VaadinSession directamente
            Object userIdAttr = session.getAttribute("userId");
            log.info("userId desde VaadinSession: {}", userIdAttr);

            if (userIdAttr == null) {
                // Método 2: Intentar con getUI()
                userIdAttr = getUI().map(ui -> ui.getSession().getAttribute("userId")).orElse(null);
                log.info("userId desde getUI().getSession(): {}", userIdAttr);
            }

            if (userIdAttr == null) {
                // Método 3: Debug manual de atributos conocidos
                log.error("=== DEBUG: Atributos de sesión conocidos ===");
                log.error("authToken: {}", session.getAttribute("authToken"));
                log.error("userEmail: {}", session.getAttribute("userEmail"));
                log.error("userId: {}", session.getAttribute("userId"));

                // Intentar también acceder al HttpSession subyacente
                try {
                    HttpSession httpSession = (HttpSession) session.getSession();
                    log.error("HttpSession ID: {}", httpSession.getId());
                    java.util.Enumeration<String> attributeNames = httpSession.getAttributeNames();
                    while (attributeNames.hasMoreElements()) {
                        String name = attributeNames.nextElement();
                        Object value = httpSession.getAttribute(name);
                        log.error("HttpSession - {}: {}", name, value);
                    }
                } catch (Exception e) {
                    log.error("Error accediendo a HttpSession: {}", e.getMessage());
                }
                log.error("=== FIN DEBUG ===");
                return null;
            }

            // Convertir a Long
            if (userIdAttr instanceof Long) {
                log.info("userId obtenido como Long: {}", userIdAttr);
                return (Long) userIdAttr;
            }

            if (userIdAttr instanceof Double) {
                Long userId = ((Double) userIdAttr).longValue();
                log.info("userId convertido desde Double: {}", userId);
                return userId;
            }

            if (userIdAttr instanceof Integer) {
                Long userId = ((Integer) userIdAttr).longValue();
                log.info("userId convertido desde Integer: {}", userId);
                return userId;
            }

            if (userIdAttr instanceof String) {
                try {
                    Long userId = Long.parseLong((String) userIdAttr);
                    log.info("userId convertido desde String: {}", userId);
                    return userId;
                } catch (NumberFormatException e) {
                    log.error("No se pudo convertir userId String a Long: {}", userIdAttr);
                }
            }

            log.error("userId encontrado pero tipo no reconocido: {} (clase: {})",
                    userIdAttr, userIdAttr.getClass().getName());
            return null;

        } catch (Exception e) {
            log.error("Error obteniendo userId desde la sesión", e);
            return null;
        }
    }


    private void showSuccessNotification(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showErrorNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}