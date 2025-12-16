package com.estoque.api.service;

import com.estoque.api.model.MovimentacaoEstoque;
import com.estoque.api.model.Produto;
import com.estoque.api.model.TipoMovimentacao;
import com.estoque.api.repository.MovimentacaoEstoqueRepository;
import com.estoque.api.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MovimentacaoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    // Como usar findBySku
    public void processarMovimentacao(String sku, int quantidade) {
        Produto produto = produtoRepository.findBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado pelo SKU: " + sku));
    }

    /**
     * Registra uma nova movimentação de estoque (entrada ou saída) e calcula o novo saldo.
     * @param sku O SKU do produto.
     * @param quantidade A quantidade a ser movimentada.
     * @param tipo A string "ENTRADA" ou "SAIDA".
     * @return A nova MovimentacaoEstoque salva.
     */

    @Transactional
    public MovimentacaoEstoque registrarMovimentacao(String sku, Integer quantidade, String tipo) {

        // 1. Validar e Buscar o Produto
        Produto produto = produtoRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com SKU: " + sku));

        // 2. Validar o tipo de movimentação
        TipoMovimentacao tipoMov;
        try {
            tipoMov = TipoMovimentacao.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de movimentação inválido: " + tipo + ". Use ENTRADA ou SAIDA.");
        }

        // 3. Obter o estoque atual (o saldo é mantido na própria entidade MovimentacaoEstoque)
        Optional<MovimentacaoEstoque> ultimoEstoqueOpt = movimentacaoEstoqueRepository.findTopByProdutoSkuOrderByDataMovimentacaoDesc(sku);

        // O saldo é obtido através de getSaldoFinal()
        Integer saldoAtual = ultimoEstoqueOpt.map(MovimentacaoEstoque::getSaldoFinal).orElse(0);

        // 4. Calcular o Novo Saldo
        Integer novoSaldo;
        if (tipoMov == TipoMovimentacao.ENTRADA) {
            novoSaldo = saldoAtual + quantidade;
        } else {
            // SAIDA
            if (saldoAtual < quantidade) {
                throw new IllegalArgumentException("Estoque insuficiente para a SAÍDA. Saldo atual: " + saldoAtual);
            }
            novoSaldo = saldoAtual - quantidade;
        }

        // 5. Criar e Salvar a nova Movimentação
        MovimentacaoEstoque novaMovimentacao = new MovimentacaoEstoque();
        novaMovimentacao.setProduto(produto);

        // Seta a quantidade que foi movimentada
        novaMovimentacao.setQuantidadeMovimentada(quantidade);

        // O novo saldo é salvo no campo setSaldoFinal()
        novaMovimentacao.setSaldoFinal(novoSaldo);

        novaMovimentacao.setTipo(tipoMov);
        novaMovimentacao.setDataMovimentacao(LocalDateTime.now());

        return movimentacaoEstoqueRepository.save(novaMovimentacao);
    }
}