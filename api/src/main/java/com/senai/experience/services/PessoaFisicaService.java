package com.senai.experience.services;

import com.senai.experience.entities.PessoaFisica;
import com.senai.experience.repositories.PessoaFisicaRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class PessoaFisicaService {

    private final PessoaFisicaRepository repository;
    private final PasswordEncoder passwordEncoder;

    public PessoaFisicaService(PessoaFisicaRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<PessoaFisica> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public PessoaFisica findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public PessoaFisica save(PessoaFisica pessoaFisica) {
        // Sempre encoda a senha — vem como texto puro do request
        pessoaFisica.setSenhaHash(passwordEncoder.encode(pessoaFisica.getSenhaHash()));
        return repository.save(pessoaFisica);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
