package com.denislopes.desafiobackend.desafio_itau_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class TransacaoDTO {

    private double valor;
    private OffsetDateTime dateTime;
}
