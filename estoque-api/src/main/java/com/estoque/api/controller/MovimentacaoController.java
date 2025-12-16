package com.estoque.api.controller;

import com.estoque.api.dto.MovimentacaoRequestDTO;
import com.estoque.api.dto.MovimentacaoResponseDTO;
import com.estoque.api.model.MovimentacaoEstoque;
import com.estoque.api.service.MovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estoque")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;

    private MovimentacaoResponseDTO convertToDTO(MovimentacaoEstoque entity) {
        MovimentacaoResponseDTO dto = new MovimentacaoResponseDTO();

        dto.setId(entity.getId());
        dto.setProdutoSku(entity.getProduto().getSku());
        dto.setProdutoNome(entity.getProduto().getNome());

        // O campo para a quantidade que foi movida é getQuantidadeMovimentada()
        dto.setQuantidadeMovimentada(entity.getQuantidadeMovimentada());

        // O campo para o saldo atual é getSaldoFinal()
        dto.setEstoqueAtual(entity.getSaldoFinal());

        // O Enum é convertido para String no DTO
        dto.setTipo(entity.getTipo().toString());

        dto.setDataMovimentacao(entity.getDataMovimentacao());

        return dto;
    }

    /**
     * POST /api/estoque - Registra uma nova movimentação (ENTRADA ou SAIDA).
     */
    @PostMapping
    public ResponseEntity<MovimentacaoResponseDTO> registrarMovimentacao(
            @Valid @RequestBody MovimentacaoRequestDTO requestDTO) {

        // Nota: Você pode precisar adicionar o usuário logado e observação aqui
        MovimentacaoEstoque novaMovimentacao = movimentacaoService.registrarMovimentacao(
                requestDTO.getSku(),
                requestDTO.getQuantidade(),
                requestDTO.getTipo()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(novaMovimentacao));
    }
}