package com.senai.experience.services;
import com.senai.experience.entities.PessoaJuridica;
import com.senai.experience.repositories.PessoaJuridicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class PessoaJuridicaService {
    @Autowired
    private PessoaJuridicaRepository repository;

    public List<PessoaJuridica> findAll() {
        return repository.findAll();
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