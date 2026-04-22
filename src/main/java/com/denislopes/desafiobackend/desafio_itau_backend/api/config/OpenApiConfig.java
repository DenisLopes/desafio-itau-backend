package com.denislopes.desafiobackend.desafio_itau_backend.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI desafioItauOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Desafio Itaú - Backend")
                        .description("""
                                API REST para recebimento de transações e cálculo de estatísticas
                                (count, sum, avg, min, max) sobre uma janela de tempo configurável.
                                """)
                        .version("1.0.0")
                        .license(new License().name("MIT")));
    }
}
