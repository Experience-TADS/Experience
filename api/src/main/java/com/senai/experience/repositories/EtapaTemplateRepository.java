package com.senai.experience.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.senai.experience.entities.EtapaTemplate;
import com.senai.experience.entities.StatusFabricacao;

public interface EtapaTemplateRepository extends JpaRepository<EtapaTemplate, Long> {

    Optional<EtapaTemplate>findByStatus(StatusFabricacao status);
} 
