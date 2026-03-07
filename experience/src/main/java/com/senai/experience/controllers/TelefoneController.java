package com.senai.experience.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.senai.experience.entities.Telefone;
import com.senai.experience.services.TelefoneService;

@RestController
@RequestMapping("/api/telefones")   // rota base
public class TelefoneController {

    @Autowired
    private TelefoneService telefoneService;

    @GetMapping
    public ResponseEntity<List<Telefone>> getAllTelefones() {
        return ResponseEntity.ok(telefoneService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Telefone> getTelefoneById(@PathVariable Long id) {
        Telefone telefone = telefoneService.findById(id);
        if (telefone != null) {
            return ResponseEntity.ok(telefone);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Telefone> createTelefone(@RequestBody Telefone telefone) {
        Telefone novoTelefone = telefoneService.save(telefone);
        return ResponseEntity.ok(novoTelefone);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Telefone> updateTelefone(@PathVariable Long id, @RequestBody Telefone telefone) {
        Telefone atualizado = telefoneService.update(id, telefone);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTelefone(@PathVariable Long id) {
        telefoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

   
//    public class TelefoneController {

//    //MÉTODOS GETTERS E SETTERS

//     //criar, deletar, buscar por id, burcar telefone por id do cliente, buscar todos os telefones, atualizar telefone por id
//     public Long postTelefone(Long id) {
//         this.id = id;
//         return id;
//     }

//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }

//     public int getNumero() { return numero; }
//     public void setNumero(int numero) { this.numero = numero; }}
