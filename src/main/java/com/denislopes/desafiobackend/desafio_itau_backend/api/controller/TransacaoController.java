package com.denislopes.desafiobackend.desafio_itau_backend.api.controller;

import com.denislopes.desafiobackend.desafio_itau_backend.api.dto.TransacaoRequest;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de escrita de transações.
 *
 * <p>Responsabilidade única: traduzir HTTP ↔ caso de uso. Não há {@code try/catch}
 * para regras de negócio — erros semânticos são lançados pelo serviço e
 * convertidos em respostas pelo {@link com.denislopes.desafiobackend.desafio_itau_backend.api.exception.ApiExceptionHandler}.
 */
@RestController
@RequestMapping("/transacao")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registrar(@Valid @RequestBody TransacaoRequest request) {
        transacaoService.registrar(request.toDomain());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void removerTodas() {
        transacaoService.removerTodas();
    }
}
