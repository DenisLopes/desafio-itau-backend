package com.denislopes.desafiobackend.desafio_itau_backend.domain.service;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.model.Transacao;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.repository.TransacaoRepository;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.service.impl.EstatisticaServiceImpl;
import com.denislopes.desafiobackend.desafio_itau_backend.infrastructure.config.EstatisticaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstatisticaServiceImplTest {

    private static final OffsetDateTime AGORA = OffsetDateTime.of(2025, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

    @Mock
    private TransacaoRepository repository;

    private EstatisticaServiceImpl service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(AGORA.toInstant(), ZoneOffset.UTC);
        EstatisticaProperties props = new EstatisticaProperties(Duration.ofSeconds(60));
        service = new EstatisticaServiceImpl(repository, props, clock);
    }

    @Test
    @DisplayName("retorna estatísticas zeradas quando não há transações")
    void retornaZeradoQuandoVazio() {
        when(repository.buscarDesde(any())).thenReturn(List.of());

        EstatisticaService.Estatistica est = service.calcular();

        assertThat(est.count()).isZero();
        assertThat(est.sum()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(est.avg()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(est.min()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(est.max()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("calcula count/sum/avg/min/max corretamente")
    void calculaAgregacoes() {
        when(repository.buscarDesde(any())).thenReturn(List.of(
                new Transacao(new BigDecimal("10.00"), AGORA.minusSeconds(10)),
                new Transacao(new BigDecimal("20.00"), AGORA.minusSeconds(20)),
                new Transacao(new BigDecimal("30.00"), AGORA.minusSeconds(30))
        ));

        EstatisticaService.Estatistica est = service.calcular();

        assertThat(est.count()).isEqualTo(3L);
        assertThat(est.sum()).isEqualByComparingTo(new BigDecimal("60.00"));
        assertThat(est.avg()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(est.min()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(est.max()).isEqualByComparingTo(new BigDecimal("30.00"));
    }

    @Test
    @DisplayName("lida com transação única (min == max == avg)")
    void transacaoUnica() {
        when(repository.buscarDesde(any())).thenReturn(List.of(
                new Transacao(new BigDecimal("42.50"), AGORA.minusSeconds(5))
        ));

        EstatisticaService.Estatistica est = service.calcular();

        assertThat(est.count()).isEqualTo(1L);
        assertThat(est.sum()).isEqualByComparingTo(new BigDecimal("42.50"));
        assertThat(est.avg()).isEqualByComparingTo(new BigDecimal("42.50"));
        assertThat(est.min()).isEqualByComparingTo(new BigDecimal("42.50"));
        assertThat(est.max()).isEqualByComparingTo(new BigDecimal("42.50"));
    }

    @Test
    @DisplayName("média arredonda com HALF_EVEN a 2 casas decimais")
    void arredondaMedia() {
        when(repository.buscarDesde(any())).thenReturn(List.of(
                new Transacao(new BigDecimal("10.00"), AGORA.minusSeconds(1)),
                new Transacao(new BigDecimal("10.00"), AGORA.minusSeconds(2)),
                new Transacao(new BigDecimal("10.01"), AGORA.minusSeconds(3))
        ));

        EstatisticaService.Estatistica est = service.calcular();

        // 30.01 / 3 = 10.00333... → 10.00
        assertThat(est.avg()).isEqualByComparingTo(new BigDecimal("10.00"));
    }
}
