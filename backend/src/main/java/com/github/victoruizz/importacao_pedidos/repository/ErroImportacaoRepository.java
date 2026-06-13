package com.github.victoruizz.importacao_pedidos.repository;

import com.github.victoruizz.importacao_pedidos.entity.ErroImportacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErroImportacaoRepository extends JpaRepository<ErroImportacao, Long> {
}
