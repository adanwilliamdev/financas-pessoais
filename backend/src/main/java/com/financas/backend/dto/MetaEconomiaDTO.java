package com.financas.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MetaEconomiaDTO {
    private Long id;
    
    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;
    
    @NotNull(message = "Valor da meta é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valorMeta;
    
    @NotNull(message = "Data de início é obrigatória")
    private LocalDate dataInicio;
    
    @NotNull(message = "Data de fim é obrigatória")
    @Future(message = "Data de fim deve ser futura")
    private LocalDate dataFim;
    
    private BigDecimal valorAtual;
    private Double porcentagemConclusao;
    private Boolean ativo;
}
