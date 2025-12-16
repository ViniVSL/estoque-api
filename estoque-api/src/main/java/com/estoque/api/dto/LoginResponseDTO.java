package com.estoque.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class LoginResponseDTO {
    private String token; // O Access Token (vida curta)
    private String refreshToken; // O Refresh Token (vida longa)
    private Long id;
    private String username;
    private List<String> roles;
}