package com.estoque.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "produto_id", unique = true, nullable = false)
    private Produto produto;

    private Integer quantidade;
}