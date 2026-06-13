package com.github.victoruizz.importacao_pedidos.repository;

import com.github.victoruizz.importacao_pedidos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    boolean existsByNumeroPedido(String numeroPedido);
    List<Pedido> findByImportacaoId(Long importacaoId);
}
