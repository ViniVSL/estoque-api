package com.estoque.api.controller;

import com.estoque.api.dto.ProdutoRequestDTO;
import com.estoque.api.dto.ProdutoResponseDTO;
import com.estoque.api.model.Produto;
import com.estoque.api.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos no estoque")
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // --- METODOS DE CONVERSÃO ---
    private Produto convertToEntity(ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setSku(dto.getSku());
        produto.setCategoria(dto.getCategoria());
        produto.setEstoqueMinimo(dto.getEstoqueMinimo());
        return produto;
    }

    private ProdutoResponseDTO convertToDTO(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setSku(produto.getSku());
        dto.setCategoria(produto.getCategoria());
        dto.setEstoqueMinimo(produto.getEstoqueMinimo());
        return dto;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@Valid @RequestBody ProdutoRequestDTO produtoDTO) {
        Produto novoProduto = produtoService.salvar(convertToEntity(produtoDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(novoProduto));
    }

    /**
     * GET /api/produtos - Implementa PAGINAÇÃO e ORDENAÇÃO.
     */
    @Operation(summary = "Lista todos os produtos do estoque (Requer ROLE_USER ou ROLE_ADMIN)")
    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listarTodosProdutos(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<Produto> produtosPage = produtoService.listarTodos(pageable);

        // Mapeia Page<Entity> para Page<DTO>
        Page<ProdutoResponseDTO> dtosPage = produtosPage.map(this::convertToDTO);

        return ResponseEntity.ok(dtosPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO produtoDetalhesDTO) {
        Produto produtoExistente = produtoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

        produtoExistente.setNome(produtoDetalhesDTO.getNome());
        produtoExistente.setSku(produtoDetalhesDTO.getSku());
        produtoExistente.setCategoria(produtoDetalhesDTO.getCategoria());
        produtoExistente.setEstoqueMinimo(produtoDetalhesDTO.getEstoqueMinimo());

        Produto produtoAtualizado = produtoService.salvar(produtoExistente);
        return ResponseEntity.ok(convertToDTO(produtoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ENDPOINTS DE ESTOQUE MÍNIMO

    @PutMapping("/{sku}/minimo")
    public ResponseEntity<ProdutoResponseDTO> definirEstoqueMinimo(@PathVariable String sku, @RequestParam Integer limite) {
        Produto produtoAtualizado = produtoService.definirEstoqueMinimo(sku, limite);
        return ResponseEntity.ok(convertToDTO(produtoAtualizado));
    }

    @GetMapping("/{sku}/alerta")
    public ResponseEntity<Boolean> verificarAlertaEstoque(@PathVariable String sku) {
        boolean emAlerta = produtoService.verificarAlertaEstoque(sku);
        return ResponseEntity.ok(emAlerta);
    }
}