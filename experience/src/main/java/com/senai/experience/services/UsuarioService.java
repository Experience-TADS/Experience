package com.senai.experience.services;

import com.senai.experience.entities.Usuario;
import com.senai.experience.repositories.UsuarioRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario update(Usuario usuario) {
        if (usuario.getId() != null && usuarioRepository.existsById(usuario.getId())) {
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
}
