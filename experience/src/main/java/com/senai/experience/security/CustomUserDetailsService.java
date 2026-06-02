package com.senai.experience.security;

import com.senai.experience.entities.Usuario;
import com.senai.experience.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + emailUsuario);
        }

        // Usar o role real do banco com prefixo ROLE_ exigido pelo Spring Security
        String role = usuario.getRole() != null
                ? "ROLE_" + usuario.getRole().name()   // ex: "ROLE_ADMIN"
                : "ROLE_CLIENTE";                       // fallback padrão

        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenhaHash())
                .authorities(role)
                .disabled(!usuario.isAtivo())
                .build();
    }
}
