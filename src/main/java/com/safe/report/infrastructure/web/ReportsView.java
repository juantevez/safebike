package com.safe.report.infrastructure.web;

import com.safe.report.application.service.BikeReportVaadinService;
import com.safe.user.infrastructure.adapters.input.web.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "/reports", layout = MainLayout.class) // ✅ PÁGINA PRINCIPAL
@PageTitle("Reportes de Bicicletas")
@Slf4j
public class ReportsView extends VerticalLayout {

    private final BikeReportVaadinService bikeReportService;

    @Autowired
    public ReportsView(BikeReportVaadinService bikeReportService) {
        this.bikeReportService = bikeReportService;
        initializeView();
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(true);

        // Contenido principal
        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);
        content.getStyle().set("max-width", "900px");
        content.getStyle().set("margin", "0 auto");

        // Bienvenida
        H2 welcomeTitle = new H2("¡Bienvenido al Sistema de Reportes!");
        welcomeTitle.getStyle().set("color", "var(--lumo-primary-color)");
        welcomeTitle.getStyle().set("text-align", "center");

        // Descripción
        Paragraph description = new Paragraph(
                "Genera reportes detallados en PDF con información completa de usuarios y sus bicicletas registradas. " +
                        "Los reportes incluyen datos de marcas, modelos, fechas de compra, valores y más."
        );
        description.getStyle().set("text-align", "center");
        description.getStyle().set("margin-bottom", "var(--lumo-space-xl)");

        // Sección principal de reportes
        VerticalLayout reportsSection = createReportsSection();

        content.add(welcomeTitle, description, reportsSection);
        add(content);
    }

    private VerticalLayout createReportsSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(true);
        section.getStyle().set("border", "2px solid var(--lumo-primary-color-10pct)");
        section.getStyle().set("border-radius", "var(--lumo-border-radius-l)");
        section.getStyle().set("background", "var(--lumo-contrast-5pct)");

        // Título de la sección con ícono grande
        HorizontalLayout sectionTitle = new HorizontalLayout();
        sectionTitle.setAlignItems(Alignment.CENTER);
        sectionTitle.setJustifyContentMode(JustifyContentMode.CENTER);

        VaadinIcon.CHART.create().getStyle().set("color", "var(--lumo-primary-color)");
        H2 title = new H2("📊 Reporte Completo de Bicicletas");
        title.getStyle().set("margin", "0");

        sectionTitle.add(title);

        Paragraph sectionDescription = new Paragraph(
                "Descarga un reporte completo en formato PDF con toda la información detallada."
        );
        sectionDescription.getStyle().set("text-align", "center");

        // Botones de acción
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Botón principal de descarga
        Button generatePdfButton = new Button("📥 Descargar PDF", VaadinIcon.DOWNLOAD.create());
        generatePdfButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        generatePdfButton.getStyle().set("min-width", "200px");
        generatePdfButton.addClickListener(event -> generateAndDownloadPdf());

        // Botón alternativo
        Button generateLinkButton = new Button("🔗 Generar Enlace", VaadinIcon.LINK.create());
        generateLinkButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        generateLinkButton.addClickListener(event -> generatePdfWithLink());

        buttonsLayout.add(generatePdfButton, generateLinkButton);

        section.add(sectionTitle, sectionDescription, buttonsLayout);
        return section;
    }

    private void generateAndDownloadPdf() {
        try {
            log.info("Generating bike report PDF from main reports page");

            StreamResource resource = bikeReportService.generateBikeReportPdf();

            // Descarga automática
            Anchor downloadAnchor = new Anchor(resource, "");
            downloadAnchor.getElement().setAttribute("download", true);
            downloadAnchor.getElement().getStyle().set("display", "none");

            add(downloadAnchor);
            downloadAnchor.getElement().executeJs("this.click()");

            // Limpiar después de la descarga
            getUI().ifPresent(ui -> ui.access(() -> {
                try {
                    Thread.sleep(1000);
                    remove(downloadAnchor);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));

            bikeReportService.showSuccessNotification();

        } catch (Exception e) {
            log.error("Error generating PDF from main reports page", e);
            bikeReportService.showErrorNotification(e.getMessage());
        }
    }

    private void generatePdfWithLink() {
        try {
            StreamResource resource = bikeReportService.generateBikeReportPdf();

            // Remover enlaces anteriores
            getChildren()
                    .filter(component -> component instanceof Anchor &&
                            !((Anchor) component).getElement().getStyle().get("display").equals("none"))
                    .forEach(this::remove);

            // Crear enlace visible
            Anchor downloadLink = new Anchor(resource, "📄 Descargar Reporte PDF - " +
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.getStyle().set("color", "var(--lumo-primary-color)");
            downloadLink.getStyle().set("text-decoration", "underline");
            downloadLink.getStyle().set("font-size", "var(--lumo-font-size-l)");
            downloadLink.getStyle().set("font-weight", "bold");
            downloadLink.getStyle().set("margin-top", "var(--lumo-space-l)");
            downloadLink.getStyle().set("display", "block");
            downloadLink.getStyle().set("text-align", "center");

            add(downloadLink);
            bikeReportService.showSuccessNotification();

        } catch (Exception e) {
            log.error("Error generating PDF link", e);
            bikeReportService.showErrorNotification(e.getMessage());
        }
    }
}