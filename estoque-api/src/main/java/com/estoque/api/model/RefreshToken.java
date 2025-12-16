package com.estoque.api.model;

import com.estoque.api.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relaciona o token a um único usuário
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // O valor real do token (string longa e única)
    @Column(nullable = false, unique = true)
    private String token;

    // Data de expiração do Refresh Token
    @Column(nullable = false)
    private Instant expiryDate;

    // Construtor manual para garantir a geração correta
    public RefreshToken(User user, String token, Instant expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }
}