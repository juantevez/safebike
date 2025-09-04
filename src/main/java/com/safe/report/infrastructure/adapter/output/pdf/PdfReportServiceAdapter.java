package com.safe.report.infrastructure.adapter.output.pdf;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.safe.report.domain.model.BikeReportDTO;
import com.safe.report.domain.port.out.ReportServicePort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfReportServiceAdapter implements ReportServicePort {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);

    @Override
    public byte[] generateBikeReportPdf(List<BikeReportDTO> bikeData, String userFullName) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Título del reporte
            Paragraph title = new Paragraph("REPORTE DE BICICLETAS", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Información del usuario
            Paragraph userInfo = new Paragraph(String.format("Usuario: %s", userFullName), SUBTITLE_FONT);
            userInfo.setSpacingAfter(10f);
            document.add(userInfo);

            // Fecha de generación
            Paragraph dateInfo = new Paragraph(String.format("Fecha de generación: %s",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))), NORMAL_FONT);
            dateInfo.setSpacingAfter(5f);
            document.add(dateInfo);

            // Total de bicicletas
            Paragraph totalInfo = new Paragraph(String.format("Total de bicicletas: %d", bikeData.size()), NORMAL_FONT);
            totalInfo.setSpacingAfter(20f);
            document.add(totalInfo);

            // Crear tabla
            PdfPTable table = new PdfPTable(6); // 6 columnas
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 2f, 1.5f, 2f, 2.5f, 2f}); // Anchos relativos

            // Encabezados de la tabla
            addHeaderCell(table, "ID");
            addHeaderCell(table, "Marca");
            addHeaderCell(table, "Tipo");
            addHeaderCell(table, "Modelo");
            addHeaderCell(table, "Número de Serie");
            addHeaderCell(table, "Fecha de Compra");

            // Datos de las bicicletas
            for (BikeReportDTO bike : bikeData) {
                addDataCell(table, bike.bikeId().toString());
                addDataCell(table, bike.brand() != null ? bike.brand() : "N/A");
                addDataCell(table, bike.type() != null ? bike.type() : "N/A");
                addDataCell(table, bike.model() != null ? bike.model() : "N/A");
                addDataCell(table, bike.serialNumber() != null ? bike.serialNumber() : "N/A");
                addDataCell(table, bike.purchaseDate() != null ?
                        bike.purchaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");
            }

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte PDF", e);
        }
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(new BaseColor(52, 73, 94)); // Color azul oscuro
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8f);
        table.addCell(cell);
    }

    private void addDataCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6f);
        cell.setBorderColor(BaseColor.GRAY);
        table.addCell(cell);
    }
}