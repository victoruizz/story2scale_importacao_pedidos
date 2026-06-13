package com.github.victoruizz.importacao_pedidos.validation;

import com.github.victoruizz.importacao_pedidos.dto.LinhaCsv;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ValidadorLinha {

    public List<String> validar (LinhaCsv linha){

        List<String> erros = new ArrayList<>();

        if(linha.getNumeroPedido() == null || linha.getNumeroPedido().isBlank()){
            erros.add("numero_pedido é obrigatório");
        }

        if(linha.getCliente() == null || linha.getCliente().isBlank()){
            erros.add("cliente é obrigatório");
        }

        if(linha.getDocumentoCliente() == null || linha.getDocumentoCliente().isBlank()){
            erros.add("documento_cliente é obrigatório");
        }

        if(linha.getProduto() == null || linha.getProduto().isBlank()){
            erros.add("produto é obrigatório");
        }

        try{
            int quantidade = Integer.parseInt(linha.getQuantidade().trim());
            if(quantidade <= 0){
                erros.add("quantidade deve ser maior que zero");
            }
        } catch (NumberFormatException e){
            erros.add("quantidade inválida: " + linha.getQuantidade());
        }

        try{
            BigDecimal valorUnitario = new BigDecimal(linha.getValorUnitario().trim());
            if(valorUnitario.compareTo(BigDecimal.ZERO) <= 0){
                erros.add("valor_unitario deve ser maior que zero");
            }
        } catch (NumberFormatException e) {
            erros.add("valor_unitário inválido: " + linha.getValorUnitario());
        }

        try{
            LocalDate dataPedido = LocalDate.parse(linha.getDataPedido().trim());
            if(dataPedido.isAfter(LocalDate.now())){
                erros.add("data_pedido deve ser menor que a data atual");
            }
        }catch (DateTimeParseException e){
            erros.add("data_pedido inválida: " + linha.getDataPedido());
        }

        return erros;
    }
}
