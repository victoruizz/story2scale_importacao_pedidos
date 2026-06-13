package com.github.victoruizz.importacao_pedidos.controller;

import com.github.victoruizz.importacao_pedidos.service.ImportacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/importacoes")
@RequiredArgsConstructor
public class ImportacaoController {

    private final ImportacaoService importacaoService;

    @PostMapping
    public ResponseEntity<Long> criar(@RequestParam String nomeArquivo) {
        Long id = importacaoService.criarImportacao(nomeArquivo);
        return ResponseEntity.status(201).body(id);
}
}
