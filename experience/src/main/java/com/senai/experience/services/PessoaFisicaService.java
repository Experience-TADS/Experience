package com.senai.experience.services;

import com.senai.experience.entities.PessoaFisica;
import com.senai.experience.repositories.PessoaFisicaRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PessoaFisicaService {

    private final PessoaFisicaRepository repository;
    private final PasswordEncoder passwordEncoder;

    public PessoaFisicaService(PessoaFisicaRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<PessoaFisica> findAll() {
        return repository.findAll();
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
