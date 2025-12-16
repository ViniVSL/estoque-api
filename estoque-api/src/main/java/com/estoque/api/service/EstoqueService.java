package com.estoque.api.service;

import com.estoque.api.model.Estoque;
import com.estoque.api.model.MovimentacaoEstoque;
import com.estoque.api.model.Produto;
import com.estoque.api.model.TipoMovimentacao;
import com.estoque.api.repository.EstoqueRepository;
import com.estoque.api.repository.MovimentacaoEstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    // Retorna estoque zero se não houver registro.
    public Estoque buscarPorProduto(Produto produto) {
        // Se não encontrar, retorna um objeto Estoque com quantidade 0.
        return estoqueRepository.findByProduto(produto)
                .orElseGet(() -> {
                    Estoque estoqueVazio = new Estoque();
                    estoqueVazio.setProduto(produto);
                    estoqueVazio.setQuantidade(0);
                    return estoqueVazio;
                });
    }

    @Transactional // Garante atomicidade: ou tudo ou nada.
    // O parâmetro 'tipo' agora usa o Enum TipoMovimentacao importado
    public Estoque registrarMovimentacao(Produto produto, Integer quantidade, TipoMovimentacao tipo, String usuario, String observacao) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser um valor positivo.");
        }

        // 1. Encontra ou Inicializa o registro de Estoque
        Estoque estoque = estoqueRepository.findByProduto(produto)
                .orElseGet(() -> {
                    Estoque novoEstoque = new Estoque();
                    novoEstoque.setProduto(produto);
                    novoEstoque.setQuantidade(0);
                    return novoEstoque;
                });

        // 2. Atualiza a Quantidade
        if (tipo == TipoMovimentacao.ENTRADA) {
            // A soma é segura porque estoque.getQuantidade() nunca é null
            estoque.setQuantidade(estoque.getQuantidade() + quantidade);
        } else if (tipo == TipoMovimentacao.SAIDA) {
            if (estoque.getQuantidade() < quantidade) {
                throw new IllegalStateException("Estoque insuficiente para a operação de saída. Disponível: " + estoque.getQuantidade());
            }
            estoque.setQuantidade(estoque.getQuantidade() - quantidade);
        }
        // OBS: AJUSTE foi removido da lógica, mas pode ser adicionado se necessário.

        Estoque estoqueAtualizado = estoqueRepository.save(estoque);

        // 3. Registra a Movimentação
        MovimentacaoEstoque movimentacaoEstoque = new MovimentacaoEstoque();
        movimentacaoEstoque.setProduto(produto);
        movimentacaoEstoque.setTipo(tipo);

        // Correspondem aos campos adicionados na Entidade
        movimentacaoEstoque.setQuantidadeMovimentada(quantidade);
        movimentacaoEstoque.setSaldoFinal(estoqueAtualizado.getQuantidade());
        movimentacaoEstoque.setUsuarioResponsavel(usuario);
        movimentacaoEstoque.setObservacao(observacao);

        movimentacaoEstoqueRepository.save(movimentacaoEstoque);

        return estoqueAtualizado;
    }
}