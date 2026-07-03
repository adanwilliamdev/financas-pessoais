package com.financas.backend.service;

import com.financas.backend.dto.AuthRequest;
import com.financas.backend.dto.AuthResponse;
import com.financas.backend.dto.RegisterRequest;
import com.financas.backend.model.Usuario;
import com.financas.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getSenha()
            )
        );
        
        Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());
        usuarioService.atualizarUltimoAcesso(request.getEmail());
        
        String token = jwtService.generateToken(usuario);
        
        return new AuthResponse(token, usuario.getNome(), usuario.getEmail());
    }
    
    public AuthResponse register(RegisterRequest request) {
        // Criar novo usuário diretamente (o método já verifica se o email existe)
        Usuario usuario = usuarioService.criarUsuario(
            request.getEmail(),
            request.getSenha(),
            request.getNome()
        );
        
        // Gerar token
        String token = jwtService.generateToken(usuario);
        
        return new AuthResponse(token, usuario.getNome(), usuario.getEmail());
    }
}