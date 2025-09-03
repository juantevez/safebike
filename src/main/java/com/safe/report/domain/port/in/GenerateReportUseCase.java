package com.safe.report.domain.port.in;

public interface GenerateReportUseCase {

    /**
     * Genera el reporte PDF de bicicletas del usuario logueado.
     * @return byte[] con el contenido del PDF
     */
    byte[] generateBikeReport();
}