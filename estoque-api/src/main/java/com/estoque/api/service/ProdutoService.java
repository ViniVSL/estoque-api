package com.estoque.api.service;

import com.estoque.api.model.Produto;
import com.estoque.api.model.MovimentacaoEstoque;
import com.estoque.api.repository.ProdutoRepository;
import com.estoque.api.repository.MovimentacaoEstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Transactional
    public Produto salvar(Produto produto) {
        if (produto.getPreco() !=null) {
            throw new IllegalArgumentException("O preço deve ser positivo.");
        }
        return produtoRepository.save(produto);
    }

    public Page<Produto> listarTodos(Pageable pageable) {
        return produtoRepository.findAll(pageable);
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Optional<Produto> buscarPorSku(String sku) {
        return produtoRepository.findBySku(sku);
    }

    @Transactional
    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    @Transactional
    public Produto definirEstoqueMinimo(String sku, Integer limite) {
        if (limite == null || limite < 0) {
            throw new IllegalArgumentException("O limite de estoque mínimo deve ser um valor zero ou positivo.");
        }

        Produto produto = produtoRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com SKU: " + sku));

        return produtoRepository.save(produto);
    }

    // Verifica se o estoque atual está abaixo do estoque mínimo.
    public boolean verificarAlertaEstoque(String sku) {
        Produto produto = produtoRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com SKU: " + sku));

        // 1. Usando o nome completo do metodo para obter o ÚLTIMO registro (saldo atual).
        Optional<MovimentacaoEstoque> estoqueOptional = movimentacaoEstoqueRepository
                .findTopByProdutoSkuOrderByDataMovimentacaoDesc(sku);

        if (estoqueOptional.isEmpty()) {
            // Se não há movimentação, o estoque é 0.
            return 0 < produto.getEstoqueMinimo();
        }

        // 2. O campo para saldo final na MovimentacaoEstoque é getSaldoFinal().
        Integer quantidadeAtual = estoqueOptional.get().getSaldoFinal();

        return quantidadeAtual < produto.getEstoqueMinimo();
    }

    public Object listarTodos() {
        return null;
    }
}