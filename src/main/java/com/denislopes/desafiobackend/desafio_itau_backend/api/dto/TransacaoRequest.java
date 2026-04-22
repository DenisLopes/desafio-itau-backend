package com.denislopes.desafiobackend.desafio_itau_backend.api.dto;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.model.Transacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Payload do endpoint {@code POST /transacao}.
 *
 * <p>As validações de formato (não-nulo, não-negativo) são declarativas via
 * Bean Validation. As validações de regra de negócio (data não futura) ficam
 * no serviço — separação clara entre validação estrutural e semântica.
 */
public record TransacaoRequest(
        @NotNull(message = "valor é obrigatório")
        @PositiveOrZero(message = "valor não pode ser negativo")
        BigDecimal valor,

        @NotNull(message = "dataHora é obrigatória")
        OffsetDateTime dataHora
) {

    public Transacao toDomain() {
        return new Transacao(valor, dataHora);
    }
}
