package com.senai.experience.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.senai.experience.entities.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
