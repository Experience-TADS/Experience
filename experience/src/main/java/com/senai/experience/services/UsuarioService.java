package com.senai.experience.services;

import com.senai.experience.entities.Usuario;
import com.senai.experience.repositories.UsuarioRepositury;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepositury usuarioRepositury;

    public UsuarioService(UsuarioRepositury usuarioRepositury) {
        this.usuarioRepositury = usuarioRepositury;
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepositury.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepositury.deleteById(id);
    }
    
    public Usuario findById(Long id) {
        return usuarioRepositury.findById(id).orElse(null);
    }

    public Usuario update(Usuario usuario) {
        if (usuario.getIdUsuario() != null && usuarioRepositury.existsById(usuario.getIdUsuario())) {
            return usuarioRepositury.save(usuario);
        }
        return null;
    }

    public List<Usuario> findAll() {
        return usuarioRepositury.findAll();
    }
}
