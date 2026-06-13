package com.github.victoruizz.importacao_pedidos.service;

import com.github.victoruizz.importacao_pedidos.config.RabbitConfig;
import com.github.victoruizz.importacao_pedidos.entity.Importacao;
import com.github.victoruizz.importacao_pedidos.entity.StatusImportacao;
import com.github.victoruizz.importacao_pedidos.repository.ImportacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ImportacaoService {

    private final ImportacaoRepository importacaoRepository;
    private final RabbitTemplate rabbitTemplate;

    public Long criarImportacao(String nomeArquivo){
        Importacao importacao = new Importacao();
        importacao.setNomeArquivo(nomeArquivo);
        importacao.setStatus(StatusImportacao.RECEBIDA);
        importacao.setCriadoEm(LocalDateTime.now());
        importacao.setTotalLinhas(0);
        importacao.setLinhasValidas(0);
        importacao.setLinhasInvalidas(0);

        Importacao salva = importacaoRepository.save(importacao);

        rabbitTemplate.convertAndSend(RabbitConfig.FILA_IMPORTACAO, salva.getId());

        return salva.getId();
    }

}
