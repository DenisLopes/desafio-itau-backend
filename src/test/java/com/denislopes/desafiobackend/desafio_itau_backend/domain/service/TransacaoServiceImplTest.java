package com.denislopes.desafiobackend.desafio_itau_backend.domain.service;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.exception.TransacaoInvalidaException;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.model.Transacao;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.repository.TransacaoRepository;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.service.impl.TransacaoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceImplTest {

    private static final OffsetDateTime AGORA = OffsetDateTime.of(2025, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

    @Mock
    private TransacaoRepository repository;

    private Clock clock;

    private TransacaoServiceImpl service;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(AGORA.toInstant(), ZoneOffset.UTC);
        service = new TransacaoServiceImpl(repository, clock);
    }

    @Test
    @DisplayName("persiste transação válida")
    void persisteTransacaoValida() {
        Transacao transacao = new Transacao(new BigDecimal("10.50"), AGORA.minusSeconds(5));

        service.registrar(transacao);

        ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
        verify(repository).salvar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(transacao);
    }

    @Test
    @DisplayName("aceita valor zero (limite inferior)")
    void aceitaValorZero() {
        Transacao transacao = new Transacao(BigDecimal.ZERO, AGORA);

        service.registrar(transacao);

        verify(repository).salvar(transacao);
    }

    @Test
    @DisplayName("rejeita valor negativo com 422")
    void rejeitaValorNegativo() {
        Transacao transacao = new Transacao(new BigDecimal("-0.01"), AGORA.minusSeconds(1));

        assertThatThrownBy(() -> service.registrar(transacao))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("valor");

        verify(repository, never()).salvar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("rejeita dataHora no futuro")
    void rejeitaDataFutura() {
        Transacao transacao = new Transacao(new BigDecimal("10.00"), AGORA.plusSeconds(1));

        assertThatThrownBy(() -> service.registrar(transacao))
                .isInstanceOf(TransacaoInvalidaException.class)
                .hasMessageContaining("dataHora");

        verify(repository, never()).salvar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("aceita dataHora exatamente igual ao agora")
    void aceitaDataHoraIgualAoAgora() {
        Transacao transacao = new Transacao(new BigDecimal("1.00"), AGORA);

        service.registrar(transacao);

        verify(repository).salvar(transacao);
    }

    @Test
    @DisplayName("delega remoção total ao repositório")
    void removeTodas() {
        service.removerTodas();
        verify(repository).removerTodas();
    }

    @Test
    @DisplayName("clock fixado garante determinismo")
    void clockInjetadoEhUsado() {
        Clock relogioNoFuturo = Clock.fixed(Instant.parse("2999-01-01T00:00:00Z"), ZoneOffset.UTC);
        TransacaoServiceImpl outro = new TransacaoServiceImpl(repository, relogioNoFuturo);

        // AGORA agora está no passado em relação ao clock, então deve aceitar
        outro.registrar(new Transacao(new BigDecimal("5.00"), AGORA));
        verify(repository).salvar(org.mockito.ArgumentMatchers.any(Transacao.class));
    }
}
