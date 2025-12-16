package com.estoque.api.service;

import com.estoque.api.model.MovimentacaoEstoque;
import com.estoque.api.model.Produto;
import com.estoque.api.repository.MovimentacaoEstoqueRepository;
import com.estoque.api.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovimentacaoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @InjectMocks
    private MovimentacaoService movimentacaoService;

    private Produto produto;
    private final String SKU_VALIDO = "P001";
    private final String TIPO_ENTRADA = "ENTRADA";
    private final String TIPO_SAIDA = "SAIDA";

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setSku(SKU_VALIDO);
        produto.setNome("Teclado Mecânico");
    }

    @Test
    void deveRegistrarEntradaQuandoEstoqueEZero() {
        Integer quantidadeMovimentada = 10;

        when(produtoRepository.findBySku(SKU_VALIDO)).thenReturn(Optional.of(produto));
        when(movimentacaoEstoqueRepository.findByProdutoSku(SKU_VALIDO)).thenReturn(Optional.empty());
        when(movimentacaoEstoqueRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(invocation -> {
            MovimentacaoEstoque saved = invocation.getArgument(0);
            assertEquals(10, saved.getQuantidade());
            return saved;
        });

        MovimentacaoEstoque resultado = movimentacaoService.registrarMovimentacao(SKU_VALIDO, quantidadeMovimentada, TIPO_ENTRADA);

        assertNotNull(resultado);
        assertEquals(10, resultado.getQuantidade());
    }

    @Test
    void deveRegistrarSaidaQuandoEstoqueESuficiente() {
        Integer quantidadeMovimentada = 5;
        Integer saldoInicial = 20;

        MovimentacaoEstoque ultimoRegistro = new MovimentacaoEstoque();
        ultimoRegistro.setQuantidade(saldoInicial);

        when(produtoRepository.findBySku(SKU_VALIDO)).thenReturn(Optional.of(produto));
        when(movimentacaoEstoqueRepository.findByProdutoSku(SKU_VALIDO)).thenReturn(Optional.of(ultimoRegistro));

        when(movimentacaoEstoqueRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(invocation -> {
            MovimentacaoEstoque saved = invocation.getArgument(0);
            assertEquals(15, saved.getQuantidade());
            return saved;
        });

        MovimentacaoEstoque resultado = movimentacaoService.registrarMovimentacao(SKU_VALIDO, quantidadeMovimentada, TIPO_SAIDA);

        assertNotNull(resultado);
        assertEquals(15, resultado.getQuantidade());
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueEInsuficienteParaSaida() {
        Integer quantidadeMovimentada = 10;
        Integer saldoInicial = 5;

        MovimentacaoEstoque ultimoRegistro = new MovimentacaoEstoque();
        ultimoRegistro.setQuantidade(saldoInicial);

        when(produtoRepository.findBySku(SKU_VALIDO)).thenReturn(Optional.of(produto));
        when(movimentacaoEstoqueRepository.findByProdutoSku(SKU_VALIDO)).thenReturn(Optional.of(ultimoRegistro));

        assertThrows(IllegalArgumentException.class, () -> {
            movimentacaoService.registrarMovimentacao(SKU_VALIDO, quantidadeMovimentada, TIPO_SAIDA);
        }, "Deveria ter lançado IllegalArgumentException por estoque insuficiente.");

        verify(movimentacaoEstoqueRepository, never()).save(any(MovimentacaoEstoque.class));
    }

    @Test
    void deveLancarExcecaoSeProdutoNaoForEncontrado() {
        String skuInvalido = "SKU_404";
        when(produtoRepository.findBySku(skuInvalido)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            movimentacaoService.registrarMovimentacao(skuInvalido, 1, TIPO_ENTRADA);
        }, "Deveria ter lançado IllegalArgumentException pois o produto não existe.");
    }

    @Test
    void deveLancarExcecaoSeTipoDeMovimentacaoForInvalido() {
        String tipoInvalido = "AJUSTE";
        when(produtoRepository.findBySku(SKU_VALIDO)).thenReturn(Optional.of(produto));

        assertThrows(IllegalArgumentException.class, () -> {
            movimentacaoService.registrarMovimentacao(SKU_VALIDO, 1, tipoInvalido);
        }, "Deveria ter lançado IllegalArgumentException pois o tipo 'AJUSTE' é inválido.");
    }
}