package com.github.victoruizz.importacao_pedidos.repository;

import com.github.victoruizz.importacao_pedidos.entity.ErroImportacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ErroImportacaoRepository extends JpaRepository<ErroImportacao, Long> {
    List<ErroImportacao> findByImportacaoId(Long importacaoId);
}
