package com.estoque.api.service;

import com.estoque.api.exception.TokenRefreshException;
import com.estoque.api.entity.Erole;
import com.estoque.api.model.RefreshToken;
import com.estoque.api.entity.Role;
import com.estoque.api.entity.User;
import com.estoque.api.repository.RoleRepository;
import com.estoque.api.repository.UserRepository;
import com.estoque.api.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class RefreshTokenServiceIntegrationTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwtRefreshExpirationMs}")
    private long refreshTokenDurationMs;

    private User user;

    @BeforeEach
    void setUp() {
        // Limpeza e criação de um usuário padrão para teste
        userRepository.deleteAll();

        Role adminRole = roleRepository.findByName(Erole.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(Erole.ROLE_ADMIN);
                    return roleRepository.save(newRole);
                });

        user = new User();
        user.setUsername("testuser_token");
        user.setPassword("password");
        user.setRoles(Set.of(adminRole));
        user = userRepository.save(user);
    }

    // --- MÉTODOS DE TESTE ---

    @Test
    void deveCriarERecuperarRefreshTokenCorretamente() {
        RefreshToken token = refreshTokenService.createRefreshToken(user.getId());

        assertNotNull(token);
        assertNotNull(token.getToken());
        assertTrue(token.getExpiryDate().isAfter(Instant.now()));
        assertEquals(user.getId(), token.getUser().getId());

        RefreshToken foundToken = refreshTokenRepository.findByToken(token.getToken()).orElseThrow();
        assertEquals(token.getToken(), foundToken.getToken());
    }

    @Test
    void deveSubstituirTokenAntigoAoCriarNovo() {
        // Cria o primeiro token
        RefreshToken firstToken = refreshTokenService.createRefreshToken(user.getId());
        String firstTokenValue = firstToken.getToken();

        // Cria o segundo token (deve substituir o primeiro)
        RefreshToken secondToken = refreshTokenService.createRefreshToken(user.getId());
        String secondTokenValue = secondToken.getToken();

        // 1. Confirma que o novo token é diferente e válido
        assertNotEquals(firstTokenValue, secondTokenValue);
        assertTrue(refreshTokenRepository.findByToken(secondTokenValue).isPresent(), "O segundo token deve estar no DB.");

        // 2. Confirma que o token antigo foi deletado (substituído)
        assertFalse(refreshTokenRepository.findByToken(firstTokenValue).isPresent(), "O primeiro token deve ter sido removido.");
    }

    @Test
    void deveLancarExcecaoQuandoTokenExpirar() {
        // 1. Cria um token com data de expiração no passado
        final RefreshToken expiredToken = new RefreshToken(
                user,
                "ExpiredUUID",
                Instant.now().minusMillis(1000) // Expirado há 1 segundo
        );

        refreshTokenRepository.save(expiredToken);

        // 2. Verifica a expiração (deve falhar)
        assertThrows(TokenRefreshException.class, () -> {
            refreshTokenService.verifyExpiration(expiredToken);
        }, "Deve lançar TokenRefreshException se o token estiver expirado.");

        // 3. Confirma que o token expirado foi deletado do DB
        assertFalse(refreshTokenService.findByToken(expiredToken.getToken()).isPresent(),
                "O token expirado deve ser removido do banco de dados.");
    }
}