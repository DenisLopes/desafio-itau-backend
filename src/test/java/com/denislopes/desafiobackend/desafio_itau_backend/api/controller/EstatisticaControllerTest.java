package com.denislopes.desafiobackend.desafio_itau_backend.api.controller;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.service.EstatisticaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstatisticaController.class)
class EstatisticaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EstatisticaService service;

    @Test
    void get_retornaEstatisticas() throws Exception {
        when(service.calcular()).thenReturn(new EstatisticaService.Estatistica(
                3L, new BigDecimal("60.00"), new BigDecimal("20.00"),
                new BigDecimal("10.00"), new BigDecimal("30.00")));

        mvc.perform(get("/estatistica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3))
                .andExpect(jsonPath("$.sum").value(60.00))
                .andExpect(jsonPath("$.avg").value(20.00))
                .andExpect(jsonPath("$.min").value(10.00))
                .andExpect(jsonPath("$.max").value(30.00));
    }

    @Test
    void get_semTransacoes_retornaZeros() throws Exception {
        when(service.calcular()).thenReturn(EstatisticaService.Estatistica.vazia());

        mvc.perform(get("/estatistica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.sum").value(0))
                .andExpect(jsonPath("$.avg").value(0))
                .andExpect(jsonPath("$.min").value(0))
                .andExpect(jsonPath("$.max").value(0));
    }
}
