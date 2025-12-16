package com.estoque.api.repository;

import com.estoque.api.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    Optional<MovimentacaoEstoque> findTopByProdutoSkuOrderByDataMovimentacaoDesc(String sku);
}