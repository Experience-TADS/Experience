package com.senai.experience.services;

import com.senai.experience.entities.PessoaJuridica;
import com.senai.experience.repositories.PessoaJuridicaRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PessoaJuridicaService {

    private final PessoaJuridicaRepository repository;
    private final PasswordEncoder passwordEncoder;

    public PessoaJuridicaService(PessoaJuridicaRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<PessoaJuridica> findAll() {
        return repository.findAll();
    }

    public PessoaJuridica findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public PessoaJuridica save(PessoaJuridica pessoaJuridica) {
        // Sempre encoda a senha — vem como texto puro do request
        pessoaJuridica.setSenhaHash(passwordEncoder.encode(pessoaJuridica.getSenhaHash()));
        return repository.save(pessoaJuridica);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
