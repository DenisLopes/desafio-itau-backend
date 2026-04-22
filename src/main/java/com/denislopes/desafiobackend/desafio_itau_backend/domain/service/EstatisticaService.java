package com.denislopes.desafiobackend.desafio_itau_backend.domain.service;

import java.math.BigDecimal;

/**
 * Caso de uso de leitura: calcula agregações sobre as transações
 * ocorridas dentro de uma janela de tempo configurável.
 */
public interface EstatisticaService {

    Estatistica calcular();

    /**
     * Resultado imutável da agregação. Todos os campos seguem a especificação do
     * endpoint {@code GET /estatistica} do desafio.
     *
     * @param count quantidade de transações na janela
     * @param sum   soma dos valores
     * @param avg   média dos valores (zero quando não há transações)
     * @param min   menor valor (zero quando não há transações)
     * @param max   maior valor (zero quando não há transações)
     */
    record Estatistica(long count, BigDecimal sum, BigDecimal avg, BigDecimal min, BigDecimal max) {

        public static Estatistica vazia() {
            return new Estatistica(0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
}
