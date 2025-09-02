package com.safe.report.application.service;

import com.safe.report.domain.port.in.GenerateReportUseCase;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.StreamResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class BikeReportVaadinService {

    private final GenerateReportUseCase generateReportUseCase;

    /**
     * Genera un reporte PDF de bicicletas y devuelve un StreamResource para Vaadin
     * @return StreamResource para descargar el PDF
     */
    public StreamResource generateBikeReportPdf() {
        try {
            log.info("Generating bike report PDF for Vaadin...");

            byte[] pdfContent = generateReportUseCase.generateBikeReport();

            String filename = "reporte_bicicletas_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

            StreamResource resource = new StreamResource(filename,
                    () -> new ByteArrayInputStream(pdfContent));

            resource.setContentType("application/pdf");
            resource.setCacheTime(0); // No cachear el archivo

            log.info("Bike report PDF generated successfully for Vaadin download");

            return resource;

        } catch (Exception e) {
            log.error("Error generating bike report PDF for Vaadin", e);

            // Mostrar notificación de error en Vaadin
            Notification.show("Error al generar el reporte PDF: " + e.getMessage(),
                            5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);

            throw new RuntimeException("Error al generar el reporte PDF", e);
        }
    }

    /**
     * Genera el reporte PDF y devuelve directamente los bytes
     * Útil para casos donde necesites manipular el contenido antes de mostrarlo
     * @return byte array del PDF generado
     */
    public byte[] generateBikeReportBytes() {
        try {
            log.info("Generating bike report PDF bytes for Vaadin...");

            byte[] pdfContent = generateReportUseCase.generateBikeReport();

            log.info("Bike report PDF bytes generated successfully");

            return pdfContent;

        } catch (Exception e) {
            log.error("Error generating bike report PDF bytes", e);

            // Mostrar notificación de error en Vaadin
            Notification.show("Error al generar el reporte PDF: " + e.getMessage(),
                            5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);

            throw new RuntimeException("Error al generar el reporte PDF", e);
        }
    }

    /**
     * Genera el nombre del archivo PDF con timestamp
     * @return nombre del archivo con formato timestamp
     */
    public String generateReportFileName() {
        return "reporte_bicicletas_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
    }

    /**
     * Muestra notificación de éxito en Vaadin
     */
    public void showSuccessNotification() {
        Notification.show("Reporte PDF generado exitosamente",
                        3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    /**
     * Muestra notificación de error en Vaadin
     * @param errorMessage mensaje de error a mostrar
     */
    public void showErrorNotification(String errorMessage) {
        Notification.show("Error: " + errorMessage,
                        5000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}