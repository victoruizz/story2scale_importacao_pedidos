package com.github.victoruizz.importacao_pedidos.dto;

import com.github.victoruizz.importacao_pedidos.entity.ErroImportacao;
import com.github.victoruizz.importacao_pedidos.entity.Importacao;
import com.github.victoruizz.importacao_pedidos.entity.Pedido;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ImportacaoDetalheDTO {
    private Importacao importacao;
    private List<Pedido> pedidos;
    private List<ErroImportacao> erros;
}
