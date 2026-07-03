package com.financas.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "metas_economia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaEconomia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false, length = 50)
    private String categoria;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valorMeta;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valorAtual = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private LocalDate dataInicio;
    
    @Column(nullable = false)
    private LocalDate dataFim;
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @Column(name = "porcentagem_conclusao")
    private Double porcentagemConclusao = 0.0;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        calcularPorcentagem();
    }
    
    @PreUpdate
    protected void onUpdate() {
        calcularPorcentagem();
    }
    
    public void calcularPorcentagem() {
        if (valorMeta != null && valorMeta.compareTo(BigDecimal.ZERO) > 0 && valorAtual != null) {
            this.porcentagemConclusao = valorAtual.divide(valorMeta, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
            if (this.porcentagemConclusao > 100) {
                this.porcentagemConclusao = 100.0;
            }
        }
    }
}
