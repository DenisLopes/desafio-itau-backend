package com.denislopes.desafiobackend.desafio_itau_backend.domain.repository;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.model.Transacao;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Porta de saída para persistência de transações.
 *
 * <p>A camada de domínio depende apenas desta abstração (DIP). Implementações
 * concretas vivem em {@code infrastructure.repository} e podem ser substituídas
 * (in-memory, JPA, Redis, etc.) sem impacto nas regras de negócio.
 */
public interface TransacaoRepository {

    void salvar(Transacao transacao);

    void removerTodas();

    /**
     * Retorna as transações com {@code dataHora} posterior ou igual ao instante informado.
     */
    List<Transacao> buscarDesde(OffsetDateTime inicio);
}
