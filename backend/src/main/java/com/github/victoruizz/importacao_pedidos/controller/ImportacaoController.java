package com.github.victoruizz.importacao_pedidos.controller;

import com.github.victoruizz.importacao_pedidos.service.ImportacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
}
