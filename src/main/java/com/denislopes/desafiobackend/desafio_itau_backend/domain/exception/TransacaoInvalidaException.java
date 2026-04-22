package com.denislopes.desafiobackend.desafio_itau_backend.domain.exception;

/**
 * Exceção de domínio lançada quando uma transação viola uma regra de negócio
 * (valor negativo ou dataHora no futuro).
 *
 * <p>É traduzida para HTTP 422 pelo handler global da camada de API.
 */
public class TransacaoInvalidaException extends RuntimeException {

    public TransacaoInvalidaException(String message) {
        super(message);
    }
}
