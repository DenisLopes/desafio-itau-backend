package com.denislopes.desafiobackend.desafio_itau_backend.controller;

import com.denislopes.desafiobackend.desafio_itau_backend.dto.TransacaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.denislopes.desafiobackend.desafio_itau_backend.service.TransacaoService;


@RestController
@RequestMapping("/transacao")
public class TransacaoController {


    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    public ResponseEntity<Void> criarTransacoes(@RequestBody TransacaoDTO transacaoDTO) {
        try {
            transacaoService.addTransacao(transacaoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

    }
}
