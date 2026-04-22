package com.denislopes.desafiobackend.desafio_itau_backend.api.dto;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.service.EstatisticaService;

import java.math.BigDecimal;

/**
 * Payload de saída de {@code GET /estatistica}, conforme spec do desafio.
 */
public record EstatisticaResponse(long count, BigDecimal sum, BigDecimal avg, BigDecimal min, BigDecimal max) {

    public static EstatisticaResponse from(EstatisticaService.Estatistica e) {
        return new EstatisticaResponse(e.count(), e.sum(), e.avg(), e.min(), e.max());
    }
}
