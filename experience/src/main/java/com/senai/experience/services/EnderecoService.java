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

    public Endereco update(Long id, Endereco endereco) {
        Endereco existing = findById(id);
        if (existing != null) {
            existing.setCep(endereco.getCep());
            existing.setLogradouro(endereco.getLogradouro());
            existing.setNumero(endereco.getNumero());
            existing.setBairro(endereco.getBairro());
            existing.setCidade(endereco.getCidade());
            existing.setEstado(endereco.getEstado());
            return enderecoRepository.save(existing);
        }
        return null;
    }

    public void delete(Long id) {
        enderecoRepository.deleteById(id);
    }
}