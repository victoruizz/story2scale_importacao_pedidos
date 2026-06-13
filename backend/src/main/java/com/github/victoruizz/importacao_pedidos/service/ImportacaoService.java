package com.github.victoruizz.importacao_pedidos.service;

import com.github.victoruizz.importacao_pedidos.config.RabbitConfig;
import com.github.victoruizz.importacao_pedidos.dto.ImportacaoDetalheDTO;
import com.github.victoruizz.importacao_pedidos.entity.ErroImportacao;
import com.github.victoruizz.importacao_pedidos.entity.Importacao;
import com.github.victoruizz.importacao_pedidos.entity.Pedido;
import com.github.victoruizz.importacao_pedidos.entity.StatusImportacao;
import com.github.victoruizz.importacao_pedidos.repository.ErroImportacaoRepository;
import com.github.victoruizz.importacao_pedidos.repository.ImportacaoRepository;
import com.github.victoruizz.importacao_pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportacaoService {

    private final ImportacaoRepository importacaoRepository;
    private final PedidoRepository pedidoRepository;
    private final ErroImportacaoRepository erroImportacaoRepository;
    private final RabbitTemplate rabbitTemplate;

    public Long criarImportacao(MultipartFile arquivo){
        String nomeArquivo = arquivo.getOriginalFilename();
        Path destino = Paths.get("uploads", nomeArquivo);
        try{
            Files.createDirectories(destino.getParent());
            arquivo.transferTo(destino.toAbsolutePath());
        } catch (IOException e){
            throw new RuntimeException("Erro ao salvar arquivo", e);
        }

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

    public List<Importacao> listarImportacoes() {
        return importacaoRepository.findAll();
    }

    public ImportacaoDetalheDTO detalharImportacao(Long id) {
        Importacao importacao = importacaoRepository.findById(id).orElseThrow();

        List<Pedido> pedidos = pedidoRepository.findByImportacaoId(id);
        List<ErroImportacao> erros = erroImportacaoRepository.findByImportacaoId(id);

        return new ImportacaoDetalheDTO(importacao, pedidos, erros);
    }

}
