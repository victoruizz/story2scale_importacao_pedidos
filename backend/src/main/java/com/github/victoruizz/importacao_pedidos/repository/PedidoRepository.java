package com.github.victoruizz.importacao_pedidos.repository;

import com.github.victoruizz.importacao_pedidos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    boolean existsByNumeroPedido(String numeroPedido);
    List<Pedido> findByImportacaoId(Long importacaoId);

    @Query("SELECT p FROM Pedido p WHERE " +
            "(:numeroPedido IS NULL OR p.numeroPedido = :numeroPedido) AND " +
            "(:cliente IS NULL OR p.cliente = :cliente) AND " +
            "(:dataPedido IS NULL OR p.dataPedido = :dataPedido) AND " +
            "(:importacaoId IS NULL OR p.importacao.id = :importacaoId)")
    List<Pedido> buscarComFiltros(
            @Param("numeroPedido") String numeroPedido,
            @Param("cliente") String cliente,
            @Param("dataPedido") LocalDate dataPedido,
            @Param("importacaoId") Long importacaoId);
}
