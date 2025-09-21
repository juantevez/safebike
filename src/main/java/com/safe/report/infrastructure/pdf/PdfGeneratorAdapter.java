package com.safe.report.infrastructure.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.safe.loadphoto.domain.model.PhotoExif;
import com.safe.loadphoto.domain.model.PhotoFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class PdfGeneratorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorAdapter.class);
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
            document.add(new Paragraph("Archivo: " + photo.getFileName(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            if (photo.getExif() != null) {
                PhotoExif exif = photo.getExif();
                document.add(new Paragraph("Fecha: " + exif.getDateTime()));
                document.add(new Paragraph("Cámara: " + exif.getCameraMaker() + " " + exif.getCameraModel()));
                if (exif.getLatitude() != null && exif.getLongitude() != null) {
                    document.add(new Paragraph("Ubicación: " + exif.getLatitude() + ", " + exif.getLongitude()));
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
}
