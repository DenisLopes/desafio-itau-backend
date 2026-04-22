package com.denislopes.desafiobackend.desafio_itau_backend.api.controller;

import com.denislopes.desafiobackend.desafio_itau_backend.api.dto.EstatisticaResponse;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.service.EstatisticaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de leitura que expõe as estatísticas agregadas.
 */
@RestController
@RequestMapping("/estatistica")
public class EstatisticaController {

    private final EstatisticaService estatisticaService;

    public EstatisticaController(EstatisticaService estatisticaService) {
        this.estatisticaService = estatisticaService;
    }

    @GetMapping
    public EstatisticaResponse obter() {
        return EstatisticaResponse.from(estatisticaService.calcular());
    }
}
