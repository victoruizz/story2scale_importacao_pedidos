package com.github.victoruizz.importacao_pedidos.controller;

import com.github.victoruizz.importacao_pedidos.entity.Pedido;
import com.github.victoruizz.importacao_pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public List<Pedido> listar(
            @RequestParam(required = false) String numeroPedido,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPedido,
            @RequestParam(required = false) Long importacaoId) {
        return pedidoService.listarPedidos(numeroPedido, cliente, dataPedido, importacaoId);
    }
}
