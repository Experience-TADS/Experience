package com.senai.experience.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.senai.experience.entities.Endereco;
import com.senai.experience.repositories.EnderecoRepository;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    public List<Endereco> findAll() {
        return enderecoRepository.findAll();
    }

    public Endereco findById(Long id) {
        return enderecoRepository.findById(id).orElse(null);
    }

    public Endereco save(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public void delete(Long id) {
        enderecoRepository.deleteById(id);
    }
}