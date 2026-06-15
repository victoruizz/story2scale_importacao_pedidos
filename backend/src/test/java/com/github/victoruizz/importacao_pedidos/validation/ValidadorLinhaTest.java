package com.github.victoruizz.importacao_pedidos.validation;

import com.github.victoruizz.importacao_pedidos.dto.LinhaCsv;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ValidadorLinhaTest {

    private final ValidadorLinha validador = new ValidadorLinha();

    @Test
    void linhaValidaNaoDeveRetornarErros() {
        LinhaCsv linha = new LinhaCsv("PED-001", "Empresa Alfa", "12345678000199", "Notebook", "2", "3500.00", "2026-01-01");
        assertTrue(validador.validar(linha).isEmpty());
    }

    @Test
    void numeroPedidoVazioDeveRetornarErro() {
        LinhaCsv linha = new LinhaCsv("", "Empresa Alfa", "12345678000199", "Notebook", "2", "3500.00", "2026-01-01");
        assertTrue(validador.validar(linha).contains("numero_pedido é obrigatório"));
    }

    @Test
    void clienteVazioDeveRetornarErro() {
        LinhaCsv linha = new LinhaCsv("PED-001", "", "12345678000199", "Notebook", "2", "3500.00", "2026-01-01");
        assertTrue(validador.validar(linha).contains("cliente é obrigatório"));
    }

    @Test
    void quantidadeZeroDeveRetornarErro() {
        LinhaCsv linha = new LinhaCsv("PED-001", "Empresa Alfa", "12345678000199", "Notebook", "0", "3500.00", "2026-01-01");
        assertTrue(validador.validar(linha).contains("quantidade deve ser maior que zero"));
    }

    @Test
    void valorUnitarioZeroDeveRetornarErro() {
        LinhaCsv linha = new LinhaCsv("PED-001", "Empresa Alfa", "12345678000199", "Notebook", "2", "0", "2026-01-01");
        assertTrue(validador.validar(linha).contains("valor_unitario deve ser maior que zero"));
    }

    @Test
    void quantidadeNaoNumericaDeveRetornarErro() {
        LinhaCsv linha = new LinhaCsv("PED-001", "Empresa Alfa", "12345678000199", "Notebook", "abc", "3500.00", "2026-01-01");
        assertFalse(validador.validar(linha).isEmpty());
    }

    @Test
    void dataFuturaDeveRetornarErro() {
        LinhaCsv linha = new LinhaCsv("PED-001", "Empresa Alfa", "12345678000199", "Notebook", "2", "3500.00", "2099-12-31");
        assertFalse(validador.validar(linha).isEmpty());
    }
}