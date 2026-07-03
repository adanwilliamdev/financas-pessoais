package com.financas.backend.controller;

import com.financas.backend.dto.TransacaoDTO;
import com.financas.backend.model.Transacao;
import com.financas.backend.model.Usuario;
import com.financas.backend.service.ArquivoService;
import com.financas.backend.service.TransacaoService;
import com.financas.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class TransacaoController {
    
    private final TransacaoService transacaoService;
    private final UsuarioService usuarioService;
    private final ArquivoService arquivoService;
    
    @GetMapping
    public ResponseEntity<List<TransacaoDTO>> listarTransacoes(Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        return ResponseEntity.ok(transacaoService.listarTransacoes(usuario));
    }
    
    @PostMapping
    public ResponseEntity<TransacaoDTO> criarTransacao(
            @Valid @RequestBody TransacaoDTO transacaoDTO,
            Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        Transacao transacao = new Transacao();
        transacao.setUsuario(usuario);
        transacao.setDescricao(transacaoDTO.getDescricao());
        transacao.setValor(transacaoDTO.getValor());
        transacao.setData(transacaoDTO.getData());
        transacao.setCategoria(transacaoDTO.getCategoria());
        transacao.setTipo(transacaoDTO.getTipo());
        transacao.setObservacao(transacaoDTO.getObservacao());
        
        Transacao saved = transacaoService.criarTransacao(transacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(transacaoService.toDTO(saved));
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<TransacaoDTO>> listarPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        return ResponseEntity.ok(transacaoService.listarPorPeriodo(usuario, inicio, fim));
    }
    
    @GetMapping("/saldo")
    public ResponseEntity<Map<String, Object>> getSaldo(Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        
        // Data atual
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());
        
        // Calcular saldos
        BigDecimal saldoTotal = transacaoService.calcularSaldo(usuario);
        BigDecimal receitasMes = transacaoService.calcularSaldoPeriodo(usuario, inicioMes, fimMes);
        BigDecimal despesasMes = transacaoService.calcularSaldoPeriodo(usuario, inicioMes, fimMes);
        
        // Calcular receitas e despesas separadamente
        BigDecimal receitas = transacaoService.getReceitasPeriodo(usuario, inicioMes, fimMes);
        BigDecimal despesas = transacaoService.getDespesasPeriodo(usuario, inicioMes, fimMes);
        
        // Resumo por categoria
        Map<String, BigDecimal> categorias = transacaoService.getResumoPorCategoria(usuario, inicioMes, fimMes);
        
        Map<String, Object> response = new HashMap<>();
        response.put("saldoTotal", saldoTotal != null ? saldoTotal : BigDecimal.ZERO);
        response.put("receitas", receitas != null ? receitas : BigDecimal.ZERO);
        response.put("despesas", despesas != null ? despesas : BigDecimal.ZERO);
        response.put("categorias", categorias != null ? categorias : new HashMap<>());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/importar")
    public ResponseEntity<?> importarArquivo(
            @RequestParam("arquivo") MultipartFile arquivo,
            Authentication auth) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
            List<Transacao> transacoes = arquivoService.processarArquivo(arquivo, usuario);
            return ResponseEntity.ok(Map.of(
                "mensagem", "Importadas " + transacoes.size() + " transações com sucesso!",
                "quantidade", transacoes.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "erro", "Erro ao importar: " + e.getMessage()
            ));
        }
    }
}