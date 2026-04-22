package com.denislopes.desafiobackend.desafio_itau_backend.infrastructure.repository;

import com.denislopes.desafiobackend.desafio_itau_backend.domain.model.Transacao;
import com.denislopes.desafiobackend.desafio_itau_backend.domain.repository.TransacaoRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Repositório in-memory thread-safe.
 *
 * <p>O enunciado do desafio dispensa persistência durável. Ainda assim a API
 * pode receber requisições concorrentes, portanto usamos {@link CopyOnWriteArrayList}
 * para permitir leituras sem travas durante o cálculo das estatísticas.
 *
 * <p>Para trocar por JPA/Redis/etc., basta fornecer outra implementação de
 * {@link TransacaoRepository}: nenhum código de domínio precisa mudar (DIP).
 */
@Repository
public class InMemoryTransacaoRepository implements TransacaoRepository {

    private final List<Transacao> transacoes = new CopyOnWriteArrayList<>();

    @Override
    public void salvar(Transacao transacao) {
        transacoes.add(transacao);
    }

    @Override
    public void removerTodas() {
        transacoes.clear();
    }

    @Override
    public List<Transacao> buscarDesde(OffsetDateTime inicio) {
        return transacoes.stream()
                .filter(t -> !t.dataHora().isBefore(inicio))
                .toList();
    }
}
