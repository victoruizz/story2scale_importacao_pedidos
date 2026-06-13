package com.github.victoruizz.importacao_pedidos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitConfig {

    public static final String FILA_IMPORTACAO = "importacao.queue";

    @Bean
    public Queue filaImportacao() {
        return new Queue(FILA_IMPORTACAO, true);
    }
}
