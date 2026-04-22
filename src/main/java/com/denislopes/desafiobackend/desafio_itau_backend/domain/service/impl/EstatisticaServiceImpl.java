package com.denislopes.desafiobackend.desafio_itau_backend.domain.service.impl;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.model.Transacao;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.repository.TransacaoRepository;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.service.EstatisticaService;
import com.denislopes.desafiobackend.desafio_itau_backend.infrastructure.config.EstatisticaProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Implementação do caso de uso de leitura/agregação.
 *
 * <p>A janela temporal é injetada via {@link EstatisticaProperties} (OCP: o
 * comportamento pode ser reconfigurado sem modificar o código).
 */
@Service
public class EstatisticaServiceImpl implements EstatisticaService {

    /** Escala de 2 casas decimais é suficiente para valores monetários em BRL. */
    private static final int ESCALA_MONETARIA = 2;

    private final TransacaoRepository repository;
    private final EstatisticaProperties properties;
    private final Clock clock;

    public EstatisticaServiceImpl(TransacaoRepository repository,
                                  EstatisticaProperties properties,
                                  Clock clock) {
        this.repository = repository;
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    public Estatistica calcular() {
        OffsetDateTime inicio = OffsetDateTime.now(clock).minus(properties.janela());
        List<Transacao> transacoes = repository.buscarDesde(inicio);

        if (transacoes.isEmpty()) {
            return Estatistica.vazia();
        }

        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal min = transacoes.get(0).valor();
        BigDecimal max = min;

        for (Transacao t : transacoes) {
            BigDecimal valor = t.valor();
            sum = sum.add(valor);
            if (valor.compareTo(min) < 0) {
                min = valor;
            }
            if (valor.compareTo(max) > 0) {
                max = valor;
            }
        }

        long count = transacoes.size();
        BigDecimal avg = sum.divide(BigDecimal.valueOf(count), MathContext.DECIMAL64)
                .setScale(ESCALA_MONETARIA, RoundingMode.HALF_EVEN);

        return new Estatistica(
                count,
                sum.setScale(ESCALA_MONETARIA, RoundingMode.HALF_EVEN),
                avg,
                min.setScale(ESCALA_MONETARIA, RoundingMode.HALF_EVEN),
                max.setScale(ESCALA_MONETARIA, RoundingMode.HALF_EVEN)
        );
    }
}
