package com.github.victoruizz.importacao_pedidos.service;

import com.github.victoruizz.importacao_pedidos.entity.Pedido;
import com.github.victoruizz.importacao_pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPedidos(String numeroPedido, String cliente,
                                      LocalDate dataPedido, Long importacaoId) {
        return pedidoRepository.buscarComFiltros(numeroPedido, cliente, dataPedido, importacaoId);
    }
}
