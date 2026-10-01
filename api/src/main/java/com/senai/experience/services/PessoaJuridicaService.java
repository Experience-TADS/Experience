package com.senai.experience.services;

import com.senai.experience.entities.PessoaJuridica;
import com.senai.experience.repositories.PessoaJuridicaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;



@Service
public class PessoaJuridicaService {

    private final PessoaJuridicaRepository repository;
    private final PasswordEncoder passwordEncoder;

    public PessoaJuridicaService(PessoaJuridicaRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<PessoaJuridica> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public PessoaJuridica findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public PessoaJuridica save(PessoaJuridica pessoaJuridica) {
        // Encoda a senha apenas se vier como texto puro (nova entidade ou senha alterada).
        // Senhas já encodadas pelo BCrypt começam com "$2a$" — evita duplo encoding no update.
        String senha = pessoaJuridica.getSenhaHash();
        if (senha != null && !senha.startsWith("$2a$") && !senha.startsWith("$2b$")) {
            pessoaJuridica.setSenhaHash(passwordEncoder.encode(senha));
        }
        return repository.save(pessoaJuridica);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
