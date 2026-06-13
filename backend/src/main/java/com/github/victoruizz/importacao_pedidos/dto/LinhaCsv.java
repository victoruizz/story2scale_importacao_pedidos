package com.github.victoruizz.importacao_pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LinhaCsv {
    private String numeroPedido;
    private String cliente;
    private String documentoCliente;
    private String produto;
    private String quantidade;
    private String valorUnitario;
    private String dataPedido;
}
