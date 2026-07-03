package com.financas.backend.service;

import com.financas.backend.model.Transacao;
import com.financas.backend.model.Usuario;
import com.financas.backend.repository.TransacaoRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArquivoService {
    
    private final TransacaoRepository transacaoRepository;
    
    @Transactional
    public List<Transacao> processarArquivo(MultipartFile arquivo, Usuario usuario) throws Exception {
        String fileName = arquivo.getOriginalFilename();
        List<Transacao> transacoes = new ArrayList<>();
        
        if (fileName != null && fileName.toLowerCase().endsWith(".csv")) {
            transacoes = processarCSV(arquivo, usuario);
        } else if (fileName != null && fileName.toLowerCase().endsWith(".ofx")) {
            transacoes = processarOFX(arquivo, usuario);
        } else {
            throw new IllegalArgumentException("Formato de arquivo não suportado. Use CSV ou OFX.");
        }
        
        log.info("Processadas {} transações do arquivo {}", transacoes.size(), fileName);
        return transacaoRepository.saveAll(transacoes);
    }
    
    private List<Transacao> processarCSV(MultipartFile arquivo, Usuario usuario) throws Exception {
        List<Transacao> transacoes = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(arquivo.getInputStream()));
             CSVReader csvReader = new CSVReaderBuilder(reader)
                 .withSkipLines(1)
                 .build()) {
            
            String[] linha;
            while ((linha = csvReader.readNext()) != null) {
                if (linha.length >= 5) {
                    try {
                        Transacao transacao = new Transacao();
                        transacao.setUsuario(usuario);
                        transacao.setDescricao(linha[0].trim());
                        transacao.setValor(new BigDecimal(linha[1].trim().replace(",", ".")));
                        transacao.setData(LocalDate.parse(linha[2].trim(), formatter));
                        transacao.setCategoria(linha[3].trim());
                        String tipo = linha[4].trim().toUpperCase();
                        if (!tipo.equals("RECEITA") && !tipo.equals("DESPESA")) {
                            throw new IllegalArgumentException("Tipo deve ser RECEITA ou DESPESA");
                        }
                        transacao.setTipo(tipo);
                        if (linha.length > 5) {
                            transacao.setObservacao(linha[5].trim());
                        }
                        transacoes.add(transacao);
                    } catch (Exception e) {
                        log.warn("Erro ao processar linha: {}", String.join(",", linha), e);
                    }
                }
            }
        }
        return transacoes;
    }
    
    private List<Transacao> processarOFX(MultipartFile arquivo, Usuario usuario) throws Exception {
        // Implementação simplificada do parser OFX
        // Em produção, usar biblioteca específica como ofx4j
        List<Transacao> transacoes = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(arquivo.getInputStream()))) {
            String line;
            String descricao = "";
            String valor = "";
            String data = "";
            String tipo = "";
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.startsWith("<MEMO>")) {
                    descricao = line.substring(6, line.indexOf("</MEMO>"));
                } else if (line.startsWith("<TRNAMT>")) {
                    valor = line.substring(7, line.indexOf("</TRNAMT>"));
                } else if (line.startsWith("<DTPOSTED>")) {
                    data = line.substring(9, line.indexOf("</DTPOSTED>"));
                } else if (line.startsWith("<TRNTYPE>")) {
                    tipo = line.substring(8, line.indexOf("</TRNTYPE>"));
                } else if (line.startsWith("</STMTTRN>") && !descricao.isEmpty() && !valor.isEmpty() && !data.isEmpty()) {
                    try {
                        Transacao transacao = new Transacao();
                        transacao.setUsuario(usuario);
                        transacao.setDescricao(descricao);
                        transacao.setValor(new BigDecimal(valor.replace(",", ".")));
                        
                        // Formato OFX: YYYYMMDD
                        LocalDate dataTransacao = LocalDate.parse(data, DateTimeFormatter.BASIC_ISO_DATE);
                        transacao.setData(dataTransacao);
                        
                        transacao.setCategoria("OUTROS");
                        transacao.setTipo(tipo.equalsIgnoreCase("CREDIT") ? "RECEITA" : "DESPESA");
                        transacoes.add(transacao);
                        
                        // Reset
                        descricao = "";
                        valor = "";
                        data = "";
                        tipo = "";
                    } catch (Exception e) {
                        log.warn("Erro ao processar linha OFX", e);
                    }
                }
            }
        }
        
        return transacoes;
    }
}
