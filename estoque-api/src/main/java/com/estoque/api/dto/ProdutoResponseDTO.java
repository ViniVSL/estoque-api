package com.estoque.api.dto;

import lombok.Data;

@Data
public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private String sku;
    private String categoria;
    private Integer estoqueMinimo;

    // NOTA: Para um DTO de listagem completo, você poderia incluir a quantidade atual de estoque aqui.
}