package com.github.victoruizz.importacao_pedidos.controller;

import com.github.victoruizz.importacao_pedidos.dto.ImportacaoDetalheDTO;
import com.github.victoruizz.importacao_pedidos.entity.Importacao;
import com.github.victoruizz.importacao_pedidos.service.ImportacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/importacoes")
@RequiredArgsConstructor
public class ImportacaoController {

    private final ImportacaoService importacaoService;

    @PostMapping
    public ResponseEntity<Long> criar(@RequestParam("arquivo") MultipartFile arquivo) {
        Long id = importacaoService.criarImportacao(arquivo);
        return ResponseEntity.status(201).body(id);
    }

    @GetMapping
    public List<Importacao> listar() {
        return importacaoService.listarImportacoes();
    }

    @GetMapping("/{id}")
    public ImportacaoDetalheDTO detalhar(@PathVariable Long id) {
        return importacaoService.detalharImportacao(id);
    }


}
