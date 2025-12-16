package com.estoque.api.dto;

import java.time.LocalDateTime;

public class MovimentacaoResponseDTO {

    private Long id;
    private String produtoSku;
    private String produtoNome;
    private Integer quantidadeMovimentada;
    private String tipo;
    private LocalDateTime dataMovimentacao;
    private Integer estoqueAtual; // Novo campo para o saldo após a operação

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProdutoSku() {
        return produtoSku;
    }

    public void setProdutoSku(String produtoSku) {
        this.produtoSku = produtoSku;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public void setProdutoNome(String produtoNome) {
        this.produtoNome = produtoNome;
    }

    public Integer getQuantidadeMovimentada() {
        return quantidadeMovimentada;
    }

    public void setQuantidadeMovimentada(Integer quantidadeMovimentada) {
        this.quantidadeMovimentada = quantidadeMovimentada;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public Integer getEstoqueAtual() {
        return estoqueAtual;
    }

    public void setEstoqueAtual(Integer estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }
}