package com.senai.experience.services;
import com.senai.experience.entities.PessoaFisica;
import com.senai.experience.repositories.PessoaFisicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class PessoaFisicaService {
    @Autowired
    private PessoaFisicaRepository repository;  


    public Page<PessoaFisica> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
    
    public PessoaFisica findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public PessoaFisica save(PessoaFisica pessoaFisica) {
        return repository.save(pessoaFisica);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
