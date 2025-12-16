package com.estoque.api.service;

import com.estoque.api.exception.TokenRefreshException;
import com.estoque.api.model.RefreshToken;
import com.estoque.api.entity.User;
import com.estoque.api.repository.RefreshTokenRepository;
import com.estoque.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${estoque.app.jwtRefreshExpirationMs}")
    private long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Busca o token no DB
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // 2. Cria e Salva o Refresh Token
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + userId));

        // Limpa tokens antigos (garante que cada usuário tenha apenas um refresh token ativo)
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);

        // Define a expiração (agora + duração)
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        // Gera o token de renovação como um UUID
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    // 3. Verifica se o token expirou
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            // Se expirou, deleta o token do banco e lança exceção
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),
                    "Refresh token expirado. Por favor, faça login novamente.");
        }

        return token;
    }
}