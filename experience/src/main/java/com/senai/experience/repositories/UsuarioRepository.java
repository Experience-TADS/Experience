package com.senai.experience.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.senai.experience.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
}
