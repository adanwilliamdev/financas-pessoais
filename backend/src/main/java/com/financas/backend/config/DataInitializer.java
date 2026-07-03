package com.financas.backend.config;

import com.financas.backend.model.Usuario;
import com.financas.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail("admin@financas.com")) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@financas.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setNome("Administrador");
            admin.setAtivo(true);
            usuarioRepository.save(admin);
            log.info("Usuário admin criado: admin@financas.com / admin123");
        }
        
        if (!usuarioRepository.existsByEmail("usuario@financas.com")) {
            Usuario usuario = new Usuario();
            usuario.setEmail("usuario@financas.com");
            usuario.setSenha(passwordEncoder.encode("usuario123"));
            usuario.setNome("Usuário Teste");
            usuario.setAtivo(true);
            usuarioRepository.save(usuario);
            log.info("Usuário teste criado: usuario@financas.com / usuario123");
        }
    }
}
