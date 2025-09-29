package com.safe.report.infrastructure.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.safe.loadphoto.domain.model.PhotoExif;
import com.safe.loadphoto.domain.model.PhotoFile;
import com.safe.location.openstreet.model.dto.UbicacionDTO;
import com.safe.location.openstreet.service.NominatimGeocodingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class PdfGeneratorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorAdapter.class);

    // Inyectar el servicio de geocoding
    private final NominatimGeocodingService geocodingService;

    public PdfGeneratorAdapter(NominatimGeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    public byte[] generatePdf(List<PhotoFile> photos) throws Exception {
        logger.info("generatePdf {} ", photos);
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Reporte de Fotos por Bicicleta", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        logger.info("photos.size() ", photos.size());
        for (PhotoFile photo : photos) {
            //document.add(new Paragraph("Archivo: " + photo.getFileName(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            if (photo.getExif() != null) {
                PhotoExif exif = photo.getExif();
                if (exif.getDateTime() != null && !exif.getDateTime().trim().isEmpty()) {
                    document.add(new Paragraph("Fecha: " + formatearFechaExif(exif.getDateTime())));
                    //document.add(new Paragraph("Fecha: " + exif.getDateTime()));
                }
                if (exif.getCameraMaker() != null && !exif.getCameraModel().trim().isEmpty()){
                    document.add(new Paragraph("Cámara: " + exif.getCameraMaker() + " " + exif.getCameraModel()));
                }
                if (exif.getLatitude() != null && exif.getLongitude() != null) {
                    //document.add(new Paragraph("Ubicación: " + exif.getLatitude() + ", " + exif.getLongitude()));
                    document.add(new Paragraph("Ubicación: " + obtenerUbicacion(exif.getLatitude(), exif.getLongitude())));
                }
            }
            document.add(new Paragraph(" "));

            if (photo.getFileData() != null && photo.getFileData().length > 0) {
                 try {
                    Image image = Image.getInstance(photo.getFileData());
                    image.scaleToFit(500, 500);
                    image.setAlignment(Element.ALIGN_CENTER);
                    document.add(image);
                 } catch (Exception e) {
                    document.add(new Paragraph("[Error cargando imagen]", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));
                 }
            }

            document.add(new Paragraph("--------------------------------------------------"));
            document.newPage();
        }

        document.close();
        return baos.toByteArray();
    }
    private String formatearFechaExif(String fechaExif){
            if (fechaExif == null || fechaExif.trim().isEmpty()) {
                return "Fecha no disponible";
            }

            try {
                // Formato de entrada de EXIF
                DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
                // Formato de salida deseado
                DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                LocalDateTime fechaHora = LocalDateTime.parse(fechaExif, formatoEntrada);
                return fechaHora.format(formatoSalida);

            } catch (DateTimeParseException e) {
                // En caso de error, devolver el formato original
                return fechaExif;
            }
    }

    private String obtenerUbicacion(double latitude, double longitude) {
        try {
            UbicacionDTO ubicacion = geocodingService.obtenerUbicacion(latitude, longitude);

            // Si no se encuentra ubicación, usar coordenadas como fallback
            if (ubicacion.getUbicacionCompleta().isEmpty() ||
                    ubicacion.getUbicacionCompleta().equals("Ubicación no encontrada")) {
                return formatearCoordenadas(latitude, longitude);
            }

            return ubicacion.getUbicacionCompleta();

        } catch (Exception e) {
            logger.warn("Error al obtener ubicación desde OpenStreetMap para coordenadas {}, {}. Usando coordenadas como fallback",
                    latitude, longitude, e);
            // Fallback a coordenadas en caso de error
            return formatearCoordenadas(latitude, longitude);
        }
    }

    private boolean sonCoordenadasValidas(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

    private String formatearCoordenadas(double latitude, double longitude) {
        return String.format("%.6f, %.6f", latitude, longitude);
    }
}
