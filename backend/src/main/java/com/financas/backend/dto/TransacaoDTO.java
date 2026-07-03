package com.financas.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransacaoDTO {
    private Long id;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    private String descricao;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;
    
    @NotNull(message = "Data é obrigatória")
    private LocalDate data;
    
    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;
    
    @NotBlank(message = "Tipo é obrigatório")
    @Pattern(regexp = "RECEITA|DESPESA", message = "Tipo deve ser RECEITA ou DESPESA")
    private String tipo;
    
    private String observacao;
}
