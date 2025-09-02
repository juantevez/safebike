package com.safe.report.infrastructure.adapter;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.safe.bike.domain.model.Bike;
import com.safe.report.domain.model.BikeReportData;
import com.safe.report.domain.port.out.PdfGeneratorPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class PdfGeneratorAdapter implements PdfGeneratorPort {

    @Override
    public byte[] generateBikeReport(BikeReportData reportData) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // Título del reporte
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Reporte de Bicicletas y Usuarios", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Información del reporte
            Font infoFont = new Font(Font.FontFamily.HELVETICA, 12);
            document.add(new Paragraph("Fecha de generación: " + reportData.getReportGeneratedAt(), infoFont));
            document.add(new Paragraph("Total de usuarios: " + reportData.getTotalUsers(), infoFont));
            document.add(new Paragraph("Total de bicicletas: " + reportData.getTotalBikes(), infoFont));
            document.add(Chunk.NEWLINE);

            // Tabla de datos
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 2f, 1.5f, 1.5f, 1.5f, 2f, 1.5f, 1.5f});

            // Headers
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            table.addCell(new Phrase("Usuario", headerFont));
            table.addCell(new Phrase("Email", headerFont));
            table.addCell(new Phrase("Marca", headerFont));
            table.addCell(new Phrase("Tipo", headerFont));
            table.addCell(new Phrase("Modelo", headerFont));
            table.addCell(new Phrase("Nº Serie", headerFont));
            table.addCell(new Phrase("Tamaño", headerFont));
            table.addCell(new Phrase("F. Compra", headerFont));

            // Datos
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 9);
            for (Bike bike : reportData.getBikes()) {
                //table.addCell(new Phrase(bike.getUserFirstName() + " " + bike.getUserLastName(), dataFont));
                table.addCell(new Phrase(bike.getUser().getFirstName() + " " + bike.getUser().getLastName(), dataFont));
                table.addCell(new Phrase(bike.getUser().getEmail(), dataFont));
                table.addCell(new Phrase(bike.getBrand().getName(), dataFont));
                table.addCell(new Phrase(bike.getBikeType().getName(), dataFont));
                table.addCell(new Phrase(bike.getBikeModel().getName(), dataFont));
                table.addCell(new Phrase(bike.getSerialNumber(), dataFont));
                table.addCell(new Phrase(bike.getSize().getName(), dataFont));
                String purchaseDate = bike.getPurchaseDate() != null ?
                        bike.getPurchaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
                table.addCell(new Phrase(purchaseDate, dataFont));
            }

            document.add(table);
            document.close();

            log.info("PDF report generated successfully with {} bikes", reportData.getTotalBikes());
            return outputStream.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generating PDF report", e);
            throw new RuntimeException("Error al generar el reporte PDF", e);
        }
    }
}
