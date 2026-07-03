package com.financas.backend.service;

import com.financas.backend.dto.TransacaoDTO;
import com.financas.backend.model.Transacao;
import com.financas.backend.model.Usuario;
import com.financas.backend.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransacaoService {
    
    private final TransacaoRepository transacaoRepository;
    
    @Transactional
    public Transacao criarTransacao(Transacao transacao) {
        // Validar data
        if (transacao.getData() == null || transacao.getData().isBefore(LocalDate.of(1900, 1, 1))) {
            transacao.setData(LocalDate.now());
            log.warn("Data invÃ¡lida corrigida para: {}", transacao.getData());
        }
        return transacaoRepository.save(transacao);
    }
    
    public List<TransacaoDTO> listarTransacoes(Usuario usuario) {
        return transacaoRepository.findByUsuarioOrderByDataDesc(usuario)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<TransacaoDTO> listarPorPeriodo(Usuario usuario, LocalDate inicio, LocalDate fim) {
        // Validar datas
        if (inicio == null) inicio = LocalDate.of(1900, 1, 1);
        if (fim == null) fim = LocalDate.now();
        
        return transacaoRepository.findByUsuarioAndDataBetweenOrderByDataDesc(usuario, inicio, fim)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public BigDecimal calcularSaldo(Usuario usuario) {
        try {
            LocalDate inicio = LocalDate.of(1900, 1, 1);
            LocalDate fim = LocalDate.now();
            
            BigDecimal receitas = transacaoRepository.sumByUsuarioAndTipoAndDataBetween(
                usuario, "RECEITA", inicio, fim);
            BigDecimal despesas = transacaoRepository.sumByUsuarioAndTipoAndDataBetween(
                usuario, "DESPESA", inicio, fim);
            
            receitas = receitas != null ? receitas : BigDecimal.ZERO;
            despesas = despesas != null ? despesas : BigDecimal.ZERO;
            
            return receitas.subtract(despesas);
        } catch (Exception e) {
            log.error("Erro ao calcular saldo: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    public BigDecimal calcularSaldoPeriodo(Usuario usuario, LocalDate inicio, LocalDate fim) {
        try {
            if (inicio == null) inicio = LocalDate.of(1900, 1, 1);
            if (fim == null) fim = LocalDate.now();
            
            BigDecimal receitas = transacaoRepository.sumByUsuarioAndTipoAndDataBetween(
                usuario, "RECEITA", inicio, fim);
            BigDecimal despesas = transacaoRepository.sumByUsuarioAndTipoAndDataBetween(
                usuario, "DESPESA", inicio, fim);
            
            receitas = receitas != null ? receitas : BigDecimal.ZERO;
            despesas = despesas != null ? despesas : BigDecimal.ZERO;
            
            return receitas.subtract(despesas);
        } catch (Exception e) {
            log.error("Erro ao calcular saldo do perÃ­odo: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    public Map<String, BigDecimal> getResumoPorCategoria(Usuario usuario, LocalDate inicio, LocalDate fim) {
        Map<String, BigDecimal> resumo = new HashMap<>();
        try {
            if (inicio == null) inicio = LocalDate.of(1900, 1, 1);
            if (fim == null) fim = LocalDate.now();
            
            List<Object[]> despesasPorCategoria = transacaoRepository.sumByCategoriaAndTipoAndDataBetween(
                usuario, "DESPESA", inicio, fim);
            
            for (Object[] row : despesasPorCategoria) {
                String categoria = (String) row[0];
                BigDecimal total = (BigDecimal) row[1];
                resumo.put(categoria, total != null ? total : BigDecimal.ZERO);
            }
        } catch (Exception e) {
            log.error("Erro ao calcular resumo por categoria: {}", e.getMessage());
        }
        return resumo;
    }
    
    public TransacaoDTO toDTO(Transacao transacao) {
        TransacaoDTO dto = new TransacaoDTO();
        dto.setId(transacao.getId());
        dto.setDescricao(transacao.getDescricao());
        dto.setValor(transacao.getValor());
        dto.setData(transacao.getData());
        dto.setCategoria(transacao.getCategoria());
        dto.setTipo(transacao.getTipo());
        dto.setObservacao(transacao.getObservacao());
        return dto;
    }

    public BigDecimal getReceitasPeriodo(Usuario usuario, LocalDate inicio, LocalDate fim) {
        try {
            if (inicio == null) inicio = LocalDate.of(1900, 1, 1);
            if (fim == null) fim = LocalDate.now();
            
            BigDecimal receitas = transacaoRepository.sumByUsuarioAndTipoAndDataBetween(
                usuario, "RECEITA", inicio, fim);
            return receitas != null ? receitas : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("Erro ao calcular receitas do período: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    public BigDecimal getDespesasPeriodo(Usuario usuario, LocalDate inicio, LocalDate fim) {
        try {
            if (inicio == null) inicio = LocalDate.of(1900, 1, 1);
            if (fim == null) fim = LocalDate.now();
            
            BigDecimal despesas = transacaoRepository.sumByUsuarioAndTipoAndDataBetween(
                usuario, "DESPESA", inicio, fim);
            return despesas != null ? despesas : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("Erro ao calcular despesas do período: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}