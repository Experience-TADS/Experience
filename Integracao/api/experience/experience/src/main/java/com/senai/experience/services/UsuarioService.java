package com.senai.experience.services;

import com.senai.experience.entities.Usuario;
import com.senai.experience.repositories.UsuarioRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario save(Usuario usuario) {
        usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario update(Usuario usuario) {
        if (usuario.getId() == null || !usuarioRepository.existsById(usuario.getId())) {
            return null;
        }
        // Sempre encoda a nova senha — o campo vem como texto puro do request
        usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        return usuarioRepository.save(usuario);
    }

    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Valida email e senha, retorna o usuário se válido ou null se inválido
    public Usuario login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null && passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            if(!usuario.isAtivo()) return null;
            return usuario;
        }
        return null;
    }
    public Usuario ativar(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if(usuario == null) return null;
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
       }

    public Usuario desativar(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if(usuario == null) return null;
        usuario.setAtivo(false);
        return usuarioRepository.save(usuario);
    }

    public void salvarFcmToken(String email, String token) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null) {
            usuario.setFcmToken(token);
            usuarioRepository.save(usuario);
        }
    }
}
