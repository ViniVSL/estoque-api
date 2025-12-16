package com.estoque.api.controller;

import com.estoque.api.model.Produto;
import com.estoque.api.service.ProdutoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
// Importa sua classe de configuração de segurança para que o teste saiba como validar o JWT
// Importe a classe que contém seu SecurityFilterChain
@Import(com.estoque.api.security.SecurityConfig.class)
public class ProdutoControllerTest {

    // Simula a injeção do objeto que permite simular requisições HTTP
    @Autowired
    private MockMvc mockMvc;

    // Simula o serviço (a camada abaixo do Controller)
    @MockBean
    private ProdutoService produtoService;

    // Dados de teste para simular a resposta do serviço
    private final Produto produto1 = new Produto(1L, "Laptop", 5, 4500.00);
    private final Produto produto2 = new Produto(2L, "Mouse", 50, 85.00);


    // TESTES DE SEGURANÇA (JWT)


    @Test
    @DisplayName("GET /api/produtos deve retornar 401 Unauthorized sem token JWT")
    void listarProdutos_deveRetornar401_semToken() throws Exception {
        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isUnauthorized()); // Espera o status 401
    }

    @Test
    @DisplayName("GET /api/produtos deve retornar 403 Forbidden com token sem ROLE_USER ou ROLE_ADMIN")
    void listarProdutos_deveRetornar403_comRoleIncorreta() throws Exception {
        // Simula uma requisição com um token válido, mas com uma ROLE não autorizada
        mockMvc.perform(get("/api/produtos")
                        // Cria um token JWT simulado com a ROLE_GUEST
                        .with(jwt().authorities()))
                .andExpect(status().isForbidden()); // Espera o status 403
    }


    // TESTE DE SUCESSO E INTEGRAÇÃO DE DADOS


    @Test
    @DisplayName("GET /api/produtos deve retornar 200 OK e a lista de produtos com ROLE_ADMIN")
    void listarProdutos_deveRetornar200eLista_comRoleAdmin() throws Exception {
        List<Produto> produtos = Arrays.asList(produto1, produto2);
        when(produtoService.listarTodos()).thenReturn(produtos);

        // ACT & ASSERT
        mockMvc.perform(get("/api/produtos")
                        // Simula uma requisição com um token JWT que possui a ROLE_ADMIN
                        .with(jwt().authorities())
                        .contentType(MediaType.APPLICATION_JSON))

                // 1. Verifica se o status HTTP é 200
                .andExpect(status().isOk())

                // 2. Verifica o tipo de conteúdo da resposta
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // 3. Verifica o tamanho da lista e os valores específicos do JSON
                .andExpect(jsonPath("$", hasSize(2))) // Verifica se há 2 elementos na lista
                .andExpect(jsonPath("$[0].nome", is(produto1.getNome())))
                .andExpect(jsonPath("$[1].preco", is(produto2.getPreco())));
    }
}