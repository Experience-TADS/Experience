package com.senai.experience.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.senai.experience.entities.Veiculo;
import com.senai.experience.repositories.VeiculoRepository;

@Service
public class VeiculoService {
    @Autowired
    private VeiculoRepository veiculoRepository;
    public Page<Veiculo> findAll(Pageable pageable) {
        return veiculoRepository.findAll(pageable);
    }
    public Veiculo findById(Long id) {
        return veiculoRepository.findById(id).orElse(null);
    }
    public Veiculo save(Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }
    public Veiculo update(@PathVariable Long id, Veiculo veiculo) {
        Veiculo existing = findById(id);
        if (existing != null) {
            existing.setStatusVeiculo(veiculo.getStatusVeiculo());
            return veiculoRepository.save(existing);
        }
        return null;
    }
    public void delete(Long id) {
        veiculoRepository.deleteById(id);
    }
}