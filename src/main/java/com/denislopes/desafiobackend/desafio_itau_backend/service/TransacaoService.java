package com.denislopes.desafiobackend.desafio_itau_backend.service;

import com.denislopes.desafiobackend.desafio_itau_backend.dto.TransacaoDTO;
import com.denislopes.desafiobackend.desafio_itau_backend.model.Transacao;
import org.springframework.stereotype.Service;
import com.denislopes.desafiobackend.desafio_itau_backend.repository.TransacaoRepository;

import java.time.OffsetDateTime;

@Service
public class TransacaoService {

    private final TransacaoRepository repository;

    public TransacaoService(TransacaoRepository repository) {
        this.repository = repository;
    }

    public void addTransacao(TransacaoDTO transacaoDTO){

        OffsetDateTime dateTime = null == transacaoDTO.getDateTime()? null : transacaoDTO.getDateTime();

        if(transacaoDTO.getValor() < 0 || dateTime.isAfter(OffsetDateTime.now())){
            throw new IllegalArgumentException("Transação invalida");
        }

        Transacao transacao = new Transacao();
        transacao.setValor(transacaoDTO.getValor());
        transacao.setDateTime(dateTime);
        repository.save(transacao);

    }

}
