package com.senai.experience.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.senai.experience.entities.Veiculo;
import com.senai.experience.repositories.VeiculoRepository;

@Service
public class VeiculoService {
    @Autowired
    private VeiculoRepository veiculoRepository;
    public List<Veiculo> findAll() { 
        return veiculoRepository.findAll();
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