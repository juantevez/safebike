package com.safe.report.infrastructure.adapter.input.web;


import com.safe.BikeSafeApplication;
import com.safe.report.application.service.BikeReportService;
import com.safe.report.domain.model.BikeReportDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route("reports")
@PageTitle("Generador de Reportes de Bicicletas")
public class ReporteView extends VerticalLayout {

    private static final Logger log = LoggerFactory.getLogger(ReporteView.class);
    private final BikeReportService bikeReportService;
    private final NumberField userIdField;
    private final Button generateButton;
    private final Button previewButton;
    private final Grid<BikeReportDTO> bikeGrid;
    private final Paragraph summaryParagraph;

    @Autowired
    public ReporteView(BikeReportService bikeReportService) {
        log.info("ReporteView " +  bikeReportService);
        this.bikeReportService = bikeReportService;

        // Configurar layout principal
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        setPadding(true);
        setSpacing(true);

        // Título
        H1 title = new H1("Generador de Reportes de Bicicletas");
        title.getStyle().set("color", "#2c3e50");

        // Campo para ID de usuario
        userIdField = new NumberField("ID de Usuario");
        userIdField.setPlaceholder("Ingrese el ID del usuario (ej: 9)");
        userIdField.setWidth("300px");
        userIdField.setValue(9.0); // Valor por defecto basado en tu ejemplo

        // Botones
        generateButton = new Button("Generar PDF", event -> generatePdfReport());
        generateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        generateButton.setWidth("150px");

        previewButton = new Button("Vista Previa", event -> showPreview());
        previewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        previewButton.setWidth("150px");

        HorizontalLayout buttonLayout = new HorizontalLayout(generateButton, previewButton);
        buttonLayout.setSpacing(true);

        // Párrafo para mostrar resumen
        summaryParagraph = new Paragraph();
        summaryParagraph.getStyle().set("font-style", "italic");
        summaryParagraph.getStyle().set("color", "#666");

        // Grid para mostrar datos
        bikeGrid = createBikeGrid();

        // Agregar componentes al layout
        add(title, userIdField, buttonLayout, summaryParagraph, bikeGrid);
    }

    private Grid<BikeReportDTO> createBikeGrid() {
        Grid<BikeReportDTO> grid = new Grid<>(BikeReportDTO.class, false);
        grid.setVisible(false);
        grid.setWidth("100%");
        grid.setMaxHeight("400px");

        // Configurar columnas
        grid.addColumn(BikeReportDTO::bikeId).setHeader("ID").setWidth("80px");
        grid.addColumn(BikeReportDTO::brand).setHeader("Marca").setWidth("120px");
        grid.addColumn(BikeReportDTO::type).setHeader("Tipo").setWidth("100px");
        grid.addColumn(BikeReportDTO::model).setHeader("Modelo").setWidth("150px");
        grid.addColumn(BikeReportDTO::serialNumber).setHeader("Número de Serie").setWidth("180px");
        grid.addColumn(bike -> bike.purchaseDate() != null ?
                        bike.purchaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A")
                .setHeader("Fecha de Compra").setWidth("120px");

        return grid;
    }

    private void generatePdfReport() {
        Long userId = getUserId();
        log.info("=========================================");
        log.info("getUserId " +  getUserId());
        log.info("=========================================");
        if (userId == null) return;

        try {
            // Validar que el usuario tiene bicicletas
            if (!bikeReportService.userHasBikes(userId)) {
                showErrorNotification("No se encontraron bicicletas para el usuario con ID: " + userId);
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

            showSuccessNotification("Reporte PDF generado correctamente: " + fileName);

        } catch (IllegalArgumentException e) {
            showErrorNotification(e.getMessage());
        } catch (Exception e) {
            showErrorNotification("Error generando el reporte: " + e.getMessage());
        }
    }

    private void showPreview() {
        Long userId = getUserId();
        if (userId == null) return;

        try {
            // Obtener datos y resumen
            List<BikeReportDTO> bikeData = bikeReportService.getBikeDataForUser(userId);
            BikeReportService.ReportSummary summary = bikeReportService.getReportSummary(userId);

            // Actualizar resumen
            summaryParagraph.setText(summary.getSummaryText());

            // Mostrar datos en el grid
            bikeGrid.setItems(bikeData);
            bikeGrid.setVisible(true);

            // Mostrar dialog con vista previa adicional
            showPreviewDialog(summary, bikeData);

            showSuccessNotification("Vista previa cargada correctamente");

        } catch (IllegalArgumentException e) {
            showErrorNotification(e.getMessage());
            bikeGrid.setVisible(false);
            summaryParagraph.setText("");
        } catch (Exception e) {
            showErrorNotification("Error cargando vista previa: " + e.getMessage());
        }
    }

    private void showPreviewDialog(BikeReportService.ReportSummary summary, List<BikeReportDTO> bikeData) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Vista Previa del Reporte");
        dialog.setWidth("600px");
        dialog.setMaxHeight("500px");

        VerticalLayout dialogContent = new VerticalLayout();
        dialogContent.setPadding(false);
        dialogContent.setSpacing(true);

        // Información del reporte
        H3 userTitle = new H3("Usuario: " + summary.userFullName());
        Paragraph totalInfo = new Paragraph("Total de bicicletas: " + summary.totalBikes());
        Paragraph dateInfo = new Paragraph("Fecha: " + summary.getFormattedDate());

        dialogContent.add(userTitle, totalInfo, dateInfo);

        // Lista de bicicletas
        if (!bikeData.isEmpty()) {
            H3 bikesTitle = new H3("Bicicletas:");
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
        }

        dialog.add(dialogContent);

        Button closeButton = new Button("Cerrar", event -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(closeButton);

        dialog.open();
    }

    private Long getUserId() {
        if (userIdField.getValue() == null) {
            showErrorNotification("Por favor, ingrese un ID de usuario válido");
            return null;
        }
        log.info("userIdField.getValue().longValue(): " + userIdField.getValue().longValue() );
        return userIdField.getValue().longValue();
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