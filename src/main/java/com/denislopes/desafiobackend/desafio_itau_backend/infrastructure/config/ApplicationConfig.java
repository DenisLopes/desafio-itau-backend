package com.denislopes.desafiobackend.desafio_itau_backend.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Configuração central de beans de infraestrutura.
 *
 * <p>Expõe um {@link Clock} injetável para desacoplar a lógica de "agora" do
 * relógio do sistema — crucial para testar regras temporais de forma determinística.
 */
@Configuration
@EnableConfigurationProperties(EstatisticaProperties.class)
public class ApplicationConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
