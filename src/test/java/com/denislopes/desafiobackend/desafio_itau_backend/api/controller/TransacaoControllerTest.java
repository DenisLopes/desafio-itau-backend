package com.denislopes.desafiobackend.desafio_itau_backend.api.controller;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.exception.TransacaoInvalidaException;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.service.TransacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransacaoController.class)
class TransacaoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransacaoService service;

    @Test
    void postValido_retorna201() throws Exception {
        String body = """
                {"valor": 10.50, "dataHora": "2025-01-01T12:00:00-03:00"}
                """;

        mvc.perform(post("/transacao").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        verify(service).registrar(any());
    }

    @Test
    void postValorNegativo_retorna422() throws Exception {
        String body = """
                {"valor": -1.00, "dataHora": "2025-01-01T12:00:00-03:00"}
                """;

        mvc.perform(post("/transacao").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void postSemValor_retorna422() throws Exception {
        String body = """
                {"dataHora": "2025-01-01T12:00:00-03:00"}
                """;

        mvc.perform(post("/transacao").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void postSemDataHora_retorna422() throws Exception {
        String body = """
                {"valor": 10.00}
                """;

        mvc.perform(post("/transacao").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void postJsonMalformado_retorna400() throws Exception {
        mvc.perform(post("/transacao").contentType(MediaType.APPLICATION_JSON).content("{not-json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postRegraNegocioViolada_retorna422() throws Exception {
        doThrow(new TransacaoInvalidaException("dataHora não pode estar no futuro"))
                .when(service).registrar(any());

        String body = """
                {"valor": 10.00, "dataHora": "2099-01-01T12:00:00-03:00"}
                """;

        mvc.perform(post("/transacao").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void delete_retorna200() throws Exception {
        mvc.perform(delete("/transacao")).andExpect(status().isOk());
        verify(service).removerTodas();
    }
}
