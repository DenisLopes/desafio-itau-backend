package com.denislopes.desafiobackend.desafio_itau_backend.api.exception;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.exception.TransacaoInvalidaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Tradução centralizada de exceções para respostas HTTP seguindo RFC 7807 ({@link ProblemDetail}).
 *
 * <ul>
 *   <li>{@link TransacaoInvalidaException} → 422 (regra de negócio violada)</li>
 *   <li>{@link MethodArgumentNotValidException} → 422 (Bean Validation falhou — spec do desafio)</li>
 *   <li>{@link HttpMessageNotReadableException} → 400 (JSON malformado)</li>
 *   <li>{@link Exception} → 500 (fallback)</li>
 * </ul>
 *
 * <p>Decisão de design: a spec do desafio Itaú trata payload "semanticamente inválido"
 * (valor negativo, data futura) como 422. Portanto violações de Bean Validation que
 * descrevem exatamente essas regras também retornam 422. JSON sintaticamente
 * inválido permanece como 400 (Bad Request).
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(TransacaoInvalidaException.class)
    public ProblemDetail handleTransacaoInvalida(TransacaoInvalidaException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Transação inválida", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Payload inválido", detail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMalformedJson(HttpMessageNotReadableException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Requisição malformada", "JSON inválido ou ausente");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Erro inesperado", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Falha inesperada ao processar a requisição");
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        return pd;
    }
}
