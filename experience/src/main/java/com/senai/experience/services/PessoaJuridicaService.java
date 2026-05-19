package com.senai.experience.services;
import com.senai.experience.entities.PessoaJuridica;
import com.senai.experience.repositories.PessoaJuridicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;



@Service
public class PessoaJuridicaService {
    @Autowired
    private PessoaJuridicaRepository repository;

    public Page<PessoaJuridica> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
    
    public PessoaJuridica findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public PessoaJuridica save(PessoaJuridica pessoaJuridica) {
        return repository.save(pessoaJuridica);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}