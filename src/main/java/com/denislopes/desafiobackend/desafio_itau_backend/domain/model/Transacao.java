package com.denislopes.desafiobackend.desafio_itau_backend.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Entidade de domínio imutável representando uma transação financeira.
 *
 * <p>Utiliza {@link BigDecimal} para evitar erros de arredondamento inerentes
 * ao {@code double} ao operar com valores monetários, e {@link OffsetDateTime}
 * para preservar a informação de fuso horário enviada pelo cliente.
 */
public record Transacao(BigDecimal valor, OffsetDateTime dataHora) {

    public Transacao {
        Objects.requireNonNull(valor, "valor não pode ser nulo");
        Objects.requireNonNull(dataHora, "dataHora não pode ser nula");
    }
}
