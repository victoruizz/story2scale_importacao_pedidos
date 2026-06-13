package com.github.victoruizz.importacao_pedidos.repository;

import com.github.victoruizz.importacao_pedidos.entity.Importacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportacaoRepository extends JpaRepository<Importacao, Long> {
}
