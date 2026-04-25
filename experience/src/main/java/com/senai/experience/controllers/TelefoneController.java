package com.senai.experience.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.TelefoneRequest;
import com.senai.experience.DTO.response.TelefoneResponse;
import com.senai.experience.entities.Telefone;
import com.senai.experience.mappers.TelefoneMapper;
import com.senai.experience.services.TelefoneService;

@RestController
@RequestMapping("/api/telefones")   // rota base
public class TelefoneController {

    @Autowired
    private TelefoneService telefoneService;

    @GetMapping
    public ResponseEntity<List<TelefoneResponse>> getAllTelefones() {
        return ResponseEntity.ok(
            telefoneService.findAll()
                .stream()
                .map(TelefoneMapper::toResponse)
                .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TelefoneResponse> getTelefoneById(@PathVariable Long id) {
        Telefone telefone = telefoneService.findById(id);
        if (telefone != null) {
            return ResponseEntity.ok(TelefoneMapper.toResponse(telefone));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TelefoneResponse> createTelefone(@RequestBody TelefoneRequest dto) {
        Telefone telefone = TelefoneMapper.toEntity(dto);
        Telefone novoTelefone = telefoneService.save(telefone);
        return ResponseEntity.status(201).body(TelefoneMapper.toResponse(novoTelefone));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TelefoneResponse> updateTelefone(@PathVariable Long id, @RequestBody TelefoneRequest dto) {
        Telefone telefone = TelefoneMapper.toEntity(dto);
        Telefone atualizado = telefoneService.update(id, telefone);
        if (atualizado != null) {
            return ResponseEntity.ok(TelefoneMapper.toResponse(atualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTelefone(@PathVariable Long id) {
        telefoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
