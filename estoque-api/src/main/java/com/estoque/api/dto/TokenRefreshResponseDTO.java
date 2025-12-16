package com.estoque.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class TokenRefreshResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    // CAMPOS ADICIONAIS NECESSÁRIOS PARA O SIGNIN:
    private Long id;
    private String username;
    private List<String> roles;

    // Construtor completo para o SIGNIN:
    public TokenRefreshResponseDTO(String accessToken, String refreshToken, String tokenType, Long id, String username, List<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    // Construtor para o REFRESH TOKEN:
    public TokenRefreshResponseDTO(String accessToken, String refreshToken, String tokenType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
    }

    // Construtor padrão
    public TokenRefreshResponseDTO() {}
}