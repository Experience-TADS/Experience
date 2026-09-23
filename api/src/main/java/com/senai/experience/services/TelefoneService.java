package com.senai.experience.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.senai.experience.entities.Telefone;
import com.senai.experience.repositories.TelefoneRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TelefoneService {

    private final TelefoneRepository telefoneRepository;

    public Page<Telefone> findAll(Pageable pageable) {
        return telefoneRepository.findAll(pageable);
    }

    public Telefone findById(Long id) {
        return telefoneRepository.findById(id).orElse(null);
    }

    public Telefone save(Telefone telefone) {
        return telefoneRepository.save(telefone);
    }

    public Telefone update(Long id, Telefone telefone) {
        Telefone existing = findById(id);
        if (existing != null) {
            existing.setNumero(telefone.getNumero());
            return telefoneRepository.save(existing);
        }
        return null;
    }

    public void delete(Long id) {
        telefoneRepository.deleteById(id);
    }
}