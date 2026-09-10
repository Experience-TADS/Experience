package com.senai.experience.controllers;
import com.senai.experience.entities.PessoaJuridica;
import com.senai.experience.services.PessoaJuridicaService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/pessoaJuridica")
public class PessoaJuridicaController{
    private final PessoaJuridicaService service;

    public PessoaJuridicaController(PessoaJuridicaService service) {
        this.service = service;
    }

    @GetMapping
    public Page<PessoaJuridica> getAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaJuridica> getById(@PathVariable Long id) {
        PessoaJuridica pessoaJuridica = service.findById(id);
        if (pessoaJuridica != null) {
            return ResponseEntity.ok(pessoaJuridica);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public PessoaJuridica create(@RequestBody PessoaJuridica pessoaJuridica) {
        return service.save(pessoaJuridica);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaJuridica> update(@PathVariable Long id, @RequestBody PessoaJuridica pessoaJuridica) {
        PessoaJuridica existingPessoaJuridica = service.findById(id);
        if (existingPessoaJuridica != null) {
            pessoaJuridica.setId(id);
            return ResponseEntity.ok(service.save(pessoaJuridica));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}