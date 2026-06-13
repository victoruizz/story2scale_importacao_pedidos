package com.github.victoruizz.importacao_pedidos.listener;

import com.github.victoruizz.importacao_pedidos.config.RabbitConfig;
import com.github.victoruizz.importacao_pedidos.dto.LinhaCsv;
import com.github.victoruizz.importacao_pedidos.entity.ErroImportacao;
import com.github.victoruizz.importacao_pedidos.entity.Importacao;
import com.github.victoruizz.importacao_pedidos.entity.Pedido;
import com.github.victoruizz.importacao_pedidos.entity.StatusImportacao;
import com.github.victoruizz.importacao_pedidos.repository.ErroImportacaoRepository;
import com.github.victoruizz.importacao_pedidos.repository.ImportacaoRepository;
import com.github.victoruizz.importacao_pedidos.repository.PedidoRepository;
import com.github.victoruizz.importacao_pedidos.validation.ValidadorLinha;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

            Set<String> numerosVistos = new HashSet<>();

            int totalLinhas = 0;
            int linhasValidas = 0;
            int linhasInvalidas = 0;

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

                totalLinhas++;

                String numero = linha.getNumeroPedido();
                boolean duplicadoNoArquivo = numerosVistos.contains(numero);
                boolean duplicadoNoBanco = pedidoRepository.existsByNumeroPedido(numero);

                if (duplicadoNoArquivo || duplicadoNoBanco) {
                    ErroImportacao erro = new ErroImportacao();
                    erro.setImportacao(importacao);
                    erro.setLinha(i);
                    erro.setNumeroPedido(numero);
                    erro.setMensagem("numero_pedido duplicado");
                    erroImportacaoRepository.save(erro);
                    linhasInvalidas++;
                    continue;
                }

                numerosVistos.add(numero);

                List<String> erros = validadorLinha.validar(linha);
                if(erros.isEmpty()){

                    Pedido pedido = new Pedido();
                    pedido.setNumeroPedido(linha.getNumeroPedido());
                    pedido.setCliente(linha.getCliente());
                    pedido.setDocumentoCliente(linha.getDocumentoCliente());
                    pedido.setProduto(linha.getProduto());

                    int quantidade = Integer.parseInt(linha.getQuantidade().trim());
                    BigDecimal valorUnitario = new BigDecimal(linha.getValorUnitario().trim());

                    pedido.setQuantidade(quantidade);
                    pedido.setValorUnitario(valorUnitario);
                    pedido.setValorTotal(valorUnitario.multiply(BigDecimal.valueOf(quantidade)));

                    pedido.setDataPedido(LocalDate.parse(linha.getDataPedido().trim()));
                    pedido.setImportacao(importacao);
                    pedido.setCriadoEm(LocalDateTime.now());
                    linhasValidas++;
                    pedidoRepository.save(pedido);

                } else{
                    for(String mensagemErro : erros){
                        ErroImportacao erro = new ErroImportacao();
                        erro.setImportacao(importacao);
                        erro.setLinha(i);
                        erro.setNumeroPedido(linha.getNumeroPedido());
                        erro.setMensagem(mensagemErro);
                        erroImportacaoRepository.save(erro);

                    }
                    linhasInvalidas++;
                }
            }

            importacao.setTotalLinhas(totalLinhas);
            importacao.setLinhasValidas(linhasValidas);
            importacao.setLinhasInvalidas(linhasInvalidas);


            if (linhasInvalidas == 0) {
                importacao.setStatus(StatusImportacao.CONCLUIDA);
            } else {
                importacao.setStatus(StatusImportacao.CONCLUIDA_COM_ERROS);
            }

            importacaoRepository.save(importacao);
        } catch (IOException e){
            throw new RuntimeException("Erro ao ler arquivo", e);
        }

    }

}
