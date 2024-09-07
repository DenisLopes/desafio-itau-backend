package com.denislopes.desafiobackend.desafio_itau_backend.repository;

import com.denislopes.desafiobackend.desafio_itau_backend.model.Transacao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TransacaoRepository {

    private final List<Transacao> transacoes = new ArrayList<>();

    public void save(Transacao transacao){
        transacoes.add(transacao);
    }

    public List<Transacao> findAll(){
        return new ArrayList<>(transacoes);
    }

}
