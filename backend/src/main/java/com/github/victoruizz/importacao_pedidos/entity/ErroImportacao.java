package com.github.victoruizz.importacao_pedidos.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "erro_importacao")
@Getter
@Setter
@NoArgsConstructor
public class ErroImportacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "importacao_id")
    private Importacao importacao;

    private Integer linha;
    private String numeroPedido;
    private String campo;
    private String mensagem;
    private String valorRecebido;
}
