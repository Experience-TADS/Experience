package com.senai.experience.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.VeiculoRequest;
import com.senai.experience.DTO.response.VeiculoResponse;
import com.senai.experience.entities.Veiculo;
import com.senai.experience.mappers.VeiculoMapper;
import com.senai.experience.services.VeiculoService;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @GetMapping
    public List<VeiculoResponse> getAllVeiculos() {
        return veiculoService.findAll()
                .stream()
                .map(VeiculoMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponse> getVeiculoById(@PathVariable Long id) {
        Veiculo veiculo = veiculoService.findById(id);
        if (veiculo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(VeiculoMapper.toResponse(veiculo));
    }

    @PostMapping
    public ResponseEntity<VeiculoResponse> createVeiculo(@RequestBody VeiculoRequest dto) {
        Veiculo salvo = veiculoService.save(VeiculoMapper.toEntity(dto));
        return ResponseEntity.status(201).body(VeiculoMapper.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponse> updateVeiculo(@PathVariable Long id, @RequestBody VeiculoRequest dto) {
        Veiculo veiculo = VeiculoMapper.toEntity(dto);
        Veiculo atualizado = veiculoService.update(id, veiculo);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(VeiculoMapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeiculo(@PathVariable Long id) {
        veiculoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
