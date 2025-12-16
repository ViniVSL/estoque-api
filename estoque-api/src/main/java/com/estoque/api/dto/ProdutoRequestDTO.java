package com.estoque.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data // Lombok para getters, setters, toString, etc.
public class ProdutoRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O SKU é obrigatório.")
    private String sku;

    @NotBlank(message = "A categoria é obrigatória.")
    private String categoria;

    @NotNull(message = "O estoque mínimo é obrigatório.")
    @PositiveOrZero(message = "O estoque mínimo deve ser zero ou positivo.")
    private Integer estoqueMinimo;
}