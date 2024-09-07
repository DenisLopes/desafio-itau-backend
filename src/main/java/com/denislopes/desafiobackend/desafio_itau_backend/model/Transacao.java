package com.denislopes.desafiobackend.desafio_itau_backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class Transacao {

    private double valor;
    private OffsetDateTime dateTime;
}

