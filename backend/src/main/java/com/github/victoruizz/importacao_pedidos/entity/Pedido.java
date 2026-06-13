package com.github.victoruizz.importacao_pedidos.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroPedido;

    private String cliente;

    private String documentoCliente;

    private String produto;

    private int quantidade;

    private BigDecimal valorUnitario;

    private BigDecimal valorTotal;

    private LocalDate dataPedido;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "importacao_id")
    private Importacao importacao;

    private LocalDateTime criadoEm;

}
