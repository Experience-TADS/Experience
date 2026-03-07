package com.senai.experience.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.senai.experience.entities.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}