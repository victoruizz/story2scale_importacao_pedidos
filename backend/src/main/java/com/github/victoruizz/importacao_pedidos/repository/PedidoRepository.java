package com.github.victoruizz.importacao_pedidos.repository;

import com.github.victoruizz.importacao_pedidos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
