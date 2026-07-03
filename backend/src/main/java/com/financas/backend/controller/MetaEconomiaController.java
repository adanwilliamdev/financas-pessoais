package com.financas.backend.controller;

import com.financas.backend.dto.MetaEconomiaDTO;
import com.financas.backend.model.MetaEconomia;
import com.financas.backend.model.Usuario;
import com.financas.backend.service.MetaEconomiaService;
import com.financas.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/metas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MetaEconomiaController {
    
    private final MetaEconomiaService metaEconomiaService;
    private final UsuarioService usuarioService;
    
    @GetMapping
    public ResponseEntity<List<MetaEconomiaDTO>> listarMetas(Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        return ResponseEntity.ok(metaEconomiaService.listarMetas(usuario));
    }
    
    @PostMapping
    public ResponseEntity<MetaEconomiaDTO> criarMeta(
            @Valid @RequestBody MetaEconomiaDTO metaDTO,
            Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        MetaEconomia meta = new MetaEconomia();
        meta.setUsuario(usuario);
        meta.setCategoria(metaDTO.getCategoria());
        meta.setValorMeta(metaDTO.getValorMeta());
        meta.setDataInicio(metaDTO.getDataInicio());
        meta.setDataFim(metaDTO.getDataFim());
        meta.setValorAtual(metaDTO.getValorAtual() != null ? metaDTO.getValorAtual() : java.math.BigDecimal.ZERO);
        meta.setAtivo(true);
        
        MetaEconomia saved = metaEconomiaService.criarMeta(meta);
        return ResponseEntity.status(HttpStatus.CREATED).body(metaEconomiaService.toDTO(saved));
    }
    
    @PutMapping("/{id}/progresso")
    public ResponseEntity<MetaEconomiaDTO> atualizarProgresso(
            @PathVariable Long id,
            Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        return ResponseEntity.ok(metaEconomiaService.atualizarProgresso(id, usuario));
    }
}
