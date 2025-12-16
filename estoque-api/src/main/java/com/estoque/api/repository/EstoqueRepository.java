package com.estoque.api.repository;

import com.estoque.api.model.Estoque;
import com.estoque.api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByProduto(Produto produto);
}