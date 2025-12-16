package com.estoque.api.controller;

import com.estoque.api.dto.SignupRequestDTO;
import com.estoque.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Garante que o teste use um banco de dados de teste
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private String asJsonString(final Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach // Limpar o banco de dados antes de cada teste se necessário
    void setUp() {
        // Exemplo: Limpar usuários para garantir a unicidade no teste.
        // Cuidado: Dependendo da sua estratégia, pode ser necessário deletar tabelas específicas para evitar problemas de Foreign Key.
    }


    // TESTE 1: Cadastro de novo usuário com sucesso

    @Test
    void testRegisterUser_Success() throws Exception {
        SignupRequestDTO signupRequest = new SignupRequestDTO();
        signupRequest.setUsername("testuser_new");
        signupRequest.setPassword("Senha123!");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso!"));
    }


    // TESTE 2: Cadastro de usuário já existente

    @Test
    void testRegisterUser_UsernameAlreadyInUse() throws Exception {
        // Cria um usuário que será duplicado
        // Simulação: se o usuário "existinguser" já existe no seu DB de teste

        SignupRequestDTO signupRequest = new SignupRequestDTO();
        signupRequest.setUsername("existinguser");
        signupRequest.setPassword("outraSenha123!");

        // 1. Cadastra o primeiro usuário
        SignupRequestDTO firstSignup = new SignupRequestDTO();
        firstSignup.setUsername("user_to_duplicate");
        firstSignup.setPassword("Pass12345");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(firstSignup)))
                .andExpect(status().isOk());

        // 2. Tenta cadastrar o mesmo usuário
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(firstSignup)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro: Username já está em uso!"));
    }


    // TESTE 3: Cadastro com role ADMIN (Para verificar lógica de roles)

    @Test
    void testRegisterUser_WithAdminRole() throws Exception {
        SignupRequestDTO signupRequest = new SignupRequestDTO();
        signupRequest.setUsername("admin_candidate");
        signupRequest.setPassword("AdminPass123!");
        signupRequest.setRole(Collections.singleton("admin"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso!"));
    }
}