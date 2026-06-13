package com.github.victoruizz.importacao_pedidos.listener;

import com.github.victoruizz.importacao_pedidos.config.RabbitConfig;
import com.github.victoruizz.importacao_pedidos.dto.LinhaCsv;
import com.github.victoruizz.importacao_pedidos.entity.Importacao;
import com.github.victoruizz.importacao_pedidos.entity.StatusImportacao;
import com.github.victoruizz.importacao_pedidos.repository.ErroImportacaoRepository;
import com.github.victoruizz.importacao_pedidos.repository.ImportacaoRepository;
import com.github.victoruizz.importacao_pedidos.repository.PedidoRepository;
import com.github.victoruizz.importacao_pedidos.validation.ValidadorLinha;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
@AllArgsConstructor
public class ImportacaoListener {

    private final ImportacaoRepository importacaoRepository;
    private final PedidoRepository pedidoRepository;
    private final ErroImportacaoRepository erroImportacaoRepository;
    private final ValidadorLinha validadorLinha;

    @RabbitListener(queues = RabbitConfig.FILA_IMPORTACAO)
    public void processar(Long imporatacaoId){

        Importacao importacao = importacaoRepository.findById(imporatacaoId).orElseThrow();

        importacao.setStatus(StatusImportacao.PROCESSANDO);
        importacaoRepository.save(importacao);

        Path caminho = Paths.get("uploads", importacao.getNomeArquivo());
        try{
            List<String> linhas = Files.readAllLines(caminho);
            for(int i = 1; i < linhas.size(); i++){
                String linhaAtual = linhas.get(i);

                String[] campos = linhaAtual.split(",", -1);

                LinhaCsv linha = new LinhaCsv(
                        campos[0], // numeroPedido
                        campos[1], // cliente
                        campos[2], // documentoCliente
                        campos[3], // produto
                        campos[4], // quantidade
                        campos[5], // valorUnitario
                        campos[6]  // dataPedido
                );

                List<String> erros = validadorLinha.validar(linha);
                System.out.println("Linha: " + i + " - Erros: " + erros);
            }
        } catch (IOException e){
            throw new RuntimeException("Erro ao ler arquivo", e);
        }

    }

}
