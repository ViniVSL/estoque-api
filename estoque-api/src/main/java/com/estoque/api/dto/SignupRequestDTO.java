package com.estoque.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignupRequestDTO {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    private Set<String> role; // Opcional: para definir o role no cadastro

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}