package com.financas.backend.service;

import com.financas.backend.dto.MetaEconomiaDTO;
import com.financas.backend.model.MetaEconomia;
import com.financas.backend.model.Usuario;
import com.financas.backend.repository.MetaEconomiaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetaEconomiaService {
    
    private final MetaEconomiaRepository metaEconomiaRepository;
    private final TransacaoService transacaoService;
    
    @Transactional
    public MetaEconomia criarMeta(MetaEconomia meta) {
        return metaEconomiaRepository.save(meta);
    }
    
    public List<MetaEconomiaDTO> listarMetas(Usuario usuario) {
        return metaEconomiaRepository.findByUsuarioAndAtivoTrueOrderByDataFimAsc(usuario)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public MetaEconomiaDTO atualizarProgresso(Long metaId, Usuario usuario) {
        MetaEconomia meta = metaEconomiaRepository.findById(metaId)
            .orElseThrow(() -> new RuntimeException("Meta não encontrada"));
        
        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Usuário não autorizado");
        }
        
        LocalDate inicio = meta.getDataInicio();
        LocalDate fim = meta.getDataFim();
        
        BigDecimal totalGasto = transacaoService.getResumoPorCategoria(usuario, inicio, fim)
            .getOrDefault(meta.getCategoria(), BigDecimal.ZERO);
        
        meta.setValorAtual(totalGasto);
        meta.calcularPorcentagem();
        
        if (meta.getPorcentagemConclusao() >= 100) {
            meta.setAtivo(false);
        }
        
        return toDTO(metaEconomiaRepository.save(meta));
    }
    
    public MetaEconomiaDTO toDTO(MetaEconomia meta) {
        MetaEconomiaDTO dto = new MetaEconomiaDTO();
        dto.setId(meta.getId());
        dto.setCategoria(meta.getCategoria());
        dto.setValorMeta(meta.getValorMeta());
        dto.setValorAtual(meta.getValorAtual());
        dto.setDataInicio(meta.getDataInicio());
        dto.setDataFim(meta.getDataFim());
        dto.setPorcentagemConclusao(meta.getPorcentagemConclusao());
        dto.setAtivo(meta.getAtivo());
        return dto;
    }
}
