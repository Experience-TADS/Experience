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


    // criar exceção de usuário não encontrado
    // throws UsuarioNotFoundException
    @Override
    public UserDetails loadUserByUsername(String emailUsuario) {
        // Buscar o usuário no banco de dados pelo username
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + usuario);
        }

        // Retornar um objeto UserDetails com as informações do usuário
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenhaHash()) // A senha já deve estar codificada
                .roles("USER") // Defina o papel do usuário (exemplo: "USER")
                .build();
    }
}
