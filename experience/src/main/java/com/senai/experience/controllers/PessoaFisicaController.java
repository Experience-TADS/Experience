package com.senai.experience.controllers;
import com.senai.experience.entities.PessoaFisica;
import com.senai.experience.services.PessoaFisicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pessoaFisica")
public class PessoaFisicaController{
    private final PessoaFisicaService service;

    public PessoaFisicaController(PessoaFisicaService service) {
        this.service = service;
    }

    @GetMapping
    public List<PessoaFisica> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaFisica> getById(@PathVariable Long id) {
        PessoaFisica pessoaFisica = service.findById(id);
        if (pessoaFisica != null) {
            return ResponseEntity.ok(pessoaFisica);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public PessoaFisica create(@RequestBody PessoaFisica pessoaFisica) {
        return service.save(pessoaFisica);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaFisica> update(@PathVariable Long id, @RequestBody PessoaFisica pessoaFisica) {
        PessoaFisica existingPessoaFisica = service.findById(id);
        if (existingPessoaFisica != null) {
            pessoaFisica.setId(id);
            return ResponseEntity.ok(service.save(pessoaFisica));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}