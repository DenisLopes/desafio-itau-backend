package com.denislopes.desafiobackend.desafio_itau_backend.domain.service.impl;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.exception.TransacaoInvalidaException;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.model.Transacao;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.repository.TransacaoRepository;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.service.TransacaoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;

/**
 * Implementação do caso de uso de escrita.
 *
 * <p>Recebe um {@link Clock} injetado para tornar a verificação de "data no futuro"
 * determinística em testes (substitui o acoplamento estático a {@code now()}).
 */
@Service
public class TransacaoServiceImpl implements TransacaoService {

    private final TransacaoRepository repository;
    private final Clock clock;

    public TransacaoServiceImpl(TransacaoRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public void registrar(Transacao transacao) {
        validar(transacao);
        repository.salvar(transacao);
    }

    @Override
    public void removerTodas() {
        repository.removerTodas();
    }

    private void validar(Transacao transacao) {
        if (transacao.valor().compareTo(BigDecimal.ZERO) < 0) {
            throw new TransacaoInvalidaException("valor não pode ser negativo");
        }
        if (transacao.dataHora().isAfter(OffsetDateTime.now(clock))) {
            throw new TransacaoInvalidaException("dataHora não pode estar no futuro");
        }
    }
}
