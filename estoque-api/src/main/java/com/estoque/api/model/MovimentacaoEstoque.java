package com.estoque.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movimentacao_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false)
    private TipoMovimentacao tipo;

    // Campo para registrar a quantidade que foi movida
    @Column(name = "quantidade_movimentada", nullable = false)
    private Integer quantidadeMovimentada;

    // Campo para registrar o saldo final após a movimentação (auditoria)
    @Column(name = "saldo_final", nullable = false)
    private Integer saldoFinal;

    // Campos para auditoria (responsável e observação)
    @Column(name = "usuario_responsavel")
    private String usuarioResponsavel;

    @Column(name = "observacao", length = 500)
    private String observacao;

    @Column(name = "data_movimentacao", nullable = false)
    private LocalDateTime dataMovimentacao = LocalDateTime.now();
}