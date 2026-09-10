package com.senai.experience.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.senai.experience.entities.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    
    
}
