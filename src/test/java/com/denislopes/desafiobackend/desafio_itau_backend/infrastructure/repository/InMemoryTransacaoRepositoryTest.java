package com.denislopes.desafiobackend.desafio_itau_backend.infrastructure.repository;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.model.Transacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTransacaoRepositoryTest {

    private static final OffsetDateTime T0 = OffsetDateTime.of(2025, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

    private InMemoryTransacaoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransacaoRepository();
    }

    @Test
    void salvaERetorna() {
        Transacao t = new Transacao(new BigDecimal("1.00"), T0);
        repository.salvar(t);
        assertThat(repository.buscarDesde(T0.minusSeconds(1))).containsExactly(t);
    }

    @Test
    void buscaDesdeRetornaApenasTransacoesDentroDaJanela() {
        Transacao antiga = new Transacao(new BigDecimal("1.00"), T0.minusSeconds(120));
        Transacao recente = new Transacao(new BigDecimal("2.00"), T0.minusSeconds(30));

        repository.salvar(antiga);
        repository.salvar(recente);

        List<Transacao> result = repository.buscarDesde(T0.minusSeconds(60));
        assertThat(result).containsExactly(recente);
    }

    @Test
    void buscaDesdeInclusivo() {
        Transacao noLimite = new Transacao(new BigDecimal("5.00"), T0.minusSeconds(60));
        repository.salvar(noLimite);

        assertThat(repository.buscarDesde(T0.minusSeconds(60))).containsExactly(noLimite);
    }

    @Test
    void removerTodas() {
        repository.salvar(new Transacao(new BigDecimal("1.00"), T0));
        repository.salvar(new Transacao(new BigDecimal("2.00"), T0));

        repository.removerTodas();

        assertThat(repository.buscarDesde(T0.minusYears(1))).isEmpty();
    }
}
