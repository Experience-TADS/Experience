package com.senai.experience.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.senai.experience.entities.Telefone;

public interface TelefoneRepository extends JpaRepository<Telefone, Long> {
}