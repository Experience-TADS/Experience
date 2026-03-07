package com.senai.experience.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.senai.experience.entities.Telefone;
import com.senai.experience.services.TelefoneService;

@RestController
@RequestMapping("/api/telefones")   // rota base
public class TelefoneController {

    @Autowired
    private TelefoneService telefoneService;

    @GetMapping
    public List<Telefone> getAllTelefones() {
        return telefoneService.findAll();
    }

    @GetMapping("/{id}")
    public Telefone getTelefoneById(@PathVariable Long id) {
        return telefoneService.findById(id);
    }

    @PostMapping
    public Telefone createTelefone(@RequestBody Telefone telefone) {
        return telefoneService.save(telefone);
    }

    @DeleteMapping("/{id}")
    public void deleteTelefone(@PathVariable Long id) {
        telefoneService.delete(id);
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
