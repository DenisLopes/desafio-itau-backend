package com.denislopes.desafiobackend.desafio_itau_backend.infrastructure.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Configurações tipadas para o cálculo de estatísticas.
 *
 * <p>Expõe a janela de agregação (default 60 segundos, conforme spec do desafio)
 * via propriedade {@code estatistica.janela}. Aceita qualquer formato
 * suportado por {@link Duration} (ex.: {@code PT60S}, {@code 60s}, {@code 2m}).
 */
@Validated
@ConfigurationProperties(prefix = "estatistica")
public record EstatisticaProperties(@NotNull Duration janela) {

    public EstatisticaProperties {
        if (janela == null || janela.isZero() || janela.isNegative()) {
            throw new IllegalArgumentException("estatistica.janela deve ser positiva");
        }
    }
}
