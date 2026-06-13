package com.github.victoruizz.importacao_pedidos.listener;

import com.github.victoruizz.importacao_pedidos.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ImportacaoListener {

    @RabbitListener(queues = RabbitConfig.FILA_IMPORTACAO)
    public void processar(Long imporatacaoId){
        System.out.println(">>> Recebi a importação da fila: id = " + imporatacaoId);
    }

}
