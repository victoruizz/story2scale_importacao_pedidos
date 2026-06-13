package com.github.victoruizz.importacao_pedidos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "importacao")
@Getter
@Setter
@NoArgsConstructor
public class Importacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo;

    @Enumerated(EnumType.STRING)
    private StatusImportacao status;

    private Integer totalLinhas;
    private Integer linhasValidas;
    private Integer linhasInvalidas;

    private LocalDateTime criadoEm;

}
