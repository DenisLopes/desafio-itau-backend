package com.denislopes.desafiobackend.desafio_itau_backend.domain.service;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.model.Transacao;

/**
 * Caso de uso de escrita de transações.
 *
 * <p>Separado de {@code EstatisticaService} em obediência ao princípio da
 * segregação de interfaces (ISP): clientes de estatística não dependem de
 * operações de mutação.
 */
public interface TransacaoService {

    void registrar(Transacao transacao);

    void removerTodas();
}
