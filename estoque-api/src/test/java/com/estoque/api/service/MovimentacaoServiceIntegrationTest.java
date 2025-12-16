package com.estoque.api.service;

import com.estoque.api.model.Produto;
import com.estoque.api.model.MovimentacaoEstoque;
import com.estoque.api.repository.ProdutoRepository;
import com.estoque.api.repository.MovimentacaoEstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MovimentacaoServiceIntegrationTest {

    @Autowired
    private MovimentacaoService movimentacaoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    private static final String SKU_TESTE = "SKU-TESTE-123";
    private Produto produto;

    @BeforeEach
    void setUp() {
        // Limpa e garante que o produto existe antes de cada teste
        movimentacaoEstoqueRepository.deleteAll();
        produtoRepository.deleteAll();

        produto = new Produto();
        produto.setSku(SKU_TESTE);
        produto.setNome("Produto Teste");
        produtoRepository.save(produto);
    }

    @Test
    void deveRegistrarEntradaECalcularSaldoCorretamente() {
        // 1. PRIMEIRA ENTRADA
        MovimentacaoEstoque mov1 = movimentacaoService.registrarMovimentacao(SKU_TESTE, 50, "ENTRADA");

        assertNotNull(mov1);
        assertEquals(50, mov1.getQuantidadeMovimentada(), "Quantidade movimentada deve ser 50.");
        assertEquals(50, mov1.getSaldoFinal(), "Saldo final após a 1ª entrada deve ser 50.");

        // 2. SEGUNDA ENTRADA
        MovimentacaoEstoque mov2 = movimentacaoService.registrarMovimentacao(SKU_TESTE, 30, "ENTRADA");

        assertNotNull(mov2);
        assertEquals(30, mov2.getQuantidadeMovimentada(), "Quantidade movimentada deve ser 30.");
        assertEquals(80, mov2.getSaldoFinal(), "Saldo final após a 2ª entrada deve ser 80 (50 + 30).");

        // 3. Verificação do último registro no DB
        MovimentacaoEstoque ultimo = movimentacaoEstoqueRepository
                .findTopByProdutoSkuOrderByDataMovimentacaoDesc(SKU_TESTE)
                .orElseThrow(() -> new AssertionError("Último registro não encontrado."));

        assertEquals(80, ultimo.getSaldoFinal(), "O método findTop deve retornar o saldo 80.");
    }

    @Test
    void deveRegistrarSaidaEAtualizarSaldoCorretamente() {
        // SETUP: Faz uma entrada inicial para ter estoque (Saldo = 100)
        movimentacaoService.registrarMovimentacao(SKU_TESTE, 100, "ENTRADA");

        // 1. PRIMEIRA SAÍDA
        MovimentacaoEstoque mov1 = movimentacaoService.registrarMovimentacao(SKU_TESTE, 25, "SAIDA");

        assertNotNull(mov1);
        assertEquals(25, mov1.getQuantidadeMovimentada(), "Quantidade movimentada deve ser 25.");
        assertEquals(75, mov1.getSaldoFinal(), "Saldo final após a 1ª saída deve ser 75 (100 - 25).");

        // 2. SEGUNDA SAÍDA
        MovimentacaoEstoque mov2 = movimentacaoService.registrarMovimentacao(SKU_TESTE, 75, "SAIDA");

        assertNotNull(mov2);
        assertEquals(75, mov2.getQuantidadeMovimentada(), "Quantidade movimentada deve ser 75.");
        assertEquals(0, mov2.getSaldoFinal(), "Saldo final após a 2ª saída deve ser 0 (75 - 75).");
    }

    @Test
    void deveLancarExcecaoParaSaidaComEstoqueInsuficiente() {
        // Estoque inicial é zero.

        assertThrows(IllegalArgumentException.class, () -> {
            movimentacaoService.registrarMovimentacao(SKU_TESTE, 10, "SAIDA");
        }, "Deve lançar exceção ao tentar sair com estoque zero.");

        // Faz uma entrada pequena.
        movimentacaoService.registrarMovimentacao(SKU_TESTE, 5, "ENTRADA");

        // Tenta fazer uma saída maior que o estoque.
        assertThrows(IllegalArgumentException.class, () -> {
            movimentacaoService.registrarMovimentacao(SKU_TESTE, 6, "SAIDA");
        }, "Deve lançar exceção ao tentar sair com estoque insuficiente.");
    }
}