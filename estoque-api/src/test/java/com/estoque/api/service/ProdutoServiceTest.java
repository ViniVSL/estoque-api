package com.estoque.api.service;

import com.estoque.api.model.Produto;
import com.estoque.api.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// 1. Usa a extensão Mockito para inicializar os mocks
@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    // 2. Cria uma instância real do serviço e injeta os mocks nele
    @InjectMocks
    private ProdutoService produtoService;

    // 3. Cria uma simulação (mock) do repositório, para não tocar no DB real
    @Mock
    private ProdutoRepository produtoRepository;

    // Dados de teste
    private Produto produtoValido;

    @BeforeEach
    void setup() {
        produtoValido = new Produto();
        produtoValido.setId(1L);
        produtoValido.setNome("Caneta Esferográfica");
        produtoValido.setPreco(2.50);
    }

    // --- Testes de Sucesso ---

    @Test
    @DisplayName("Deve salvar o produto com sucesso e retornar o objeto salvo")
    void salvar_deveSalvarProduto_quandoProdutoValido() {
        // ARRANGE (Configuração do Mockito)
        // Quando o repositório.save for chamado com qualquer produto, retorne o produtoValido
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoValido);

        // ACT (Execução do metodo a ser testado)
        Produto salvo = produtoService.salvar(produtoValido);

        // ASSERT (Verificação)
        assertNotNull(salvo);
        assertEquals("Caneta Esferográfica", salvo.getNome());

        // Verifica se o metodo save do repositório foi realmente chamado uma vez
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve retornar o produto quando o ID for encontrado")
    void buscarPorId_deveRetornarProduto_quandoIDExiste() {
        // Quando o findById for chamado com ID 1L, retorne um Optional contendo o produtoValido
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoValido));

        Optional<Produto> resultado = produtoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(produtoRepository, times(1)).findById(1L);
    }

    // --- Testes de Falha/Exceção ---

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar produto com preço zero")
    void salvar_deveLancarExcecao_quandoPrecoInvalido() {
        produtoValido.setPreco(0.0); // Define um preço inválido para o teste

        // Verifica se o metodo lança a exceção esperada
        assertThrows(IllegalArgumentException.class, () -> {
            produtoService.salvar(produtoValido);
        });

        // Verifica que o metodo save do repositório NÃO foi chamado
        verify(produtoRepository, never()).save(any(Produto.class));
    }
}