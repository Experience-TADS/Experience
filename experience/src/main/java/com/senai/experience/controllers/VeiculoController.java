package com.senai.experience.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.senai.experience.entities.Veiculo;
import com.senai.experience.services.VeiculoService;

@RestController
@RequestMapping("/veiculos")    

public class VeiculoController {
    @Autowired
    private VeiculoService veiculoService;  

    @GetMapping
    public ResponseEntity<List<Veiculo>> getAllVeiculos() {
        return ResponseEntity.ok(veiculoService.findAll());
    }   

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> getVeiculoById(@PathVariable Long id) {
        Veiculo veiculo = veiculoService.findById(id);
        if (veiculo != null) {
            return ResponseEntity.ok(veiculo);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Veiculo> createVeiculo(@RequestBody Veiculo veiculo) {
        return ResponseEntity.ok(veiculoService.save(veiculo));
    }   

    @PutMapping("/{id}")
    public ResponseEntity<Veiculo> updateVeiculo(@PathVariable Long id, @RequestBody Veiculo veiculo) {
        Veiculo atualizado = veiculoService.update(id, veiculo);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeiculo(@PathVariable Long id) {
        veiculoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
