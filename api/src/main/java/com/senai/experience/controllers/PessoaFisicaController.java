package com.senai.experience.controllers;

import com.senai.experience.DTO.request.PessoaFisicaRequest;
import com.senai.experience.DTO.response.PessoaFisicaResponse;
import com.senai.experience.entities.PessoaFisica;
import com.senai.experience.mappers.PessoaFisicaMapper;
import com.senai.experience.services.PessoaFisicaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pessoaFisica")
@Tag(name = "Pessoa Física", description = "Gerenciamento de pessoas físicas")
public class PessoaFisicaController {

    private final PessoaFisicaService service;

    public PessoaFisicaController(PessoaFisicaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todas as pessoas físicas paginadas")
    public Page<PessoaFisicaResponse> getAll(Pageable pageable) {
        return service.findAll(pageable).map(PessoaFisicaMapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma pessoa física pelo ID")
    public ResponseEntity<PessoaFisicaResponse> getById(@PathVariable Long id) {
        PessoaFisica pessoaFisica = service.findById(id);
        if (pessoaFisica == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PessoaFisicaMapper.toResponse(pessoaFisica));
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova pessoa física")
    public ResponseEntity<PessoaFisicaResponse> create(@Valid @RequestBody PessoaFisicaRequest request) {
        PessoaFisica entity = PessoaFisicaMapper.toEntity(request);
        PessoaFisica salva = service.save(entity);
        return ResponseEntity.status(201).body(PessoaFisicaMapper.toResponse(salva));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados de uma pessoa física")
    public ResponseEntity<PessoaFisicaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PessoaFisicaRequest request) {

        PessoaFisica existing = service.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        PessoaFisica entity = PessoaFisicaMapper.toEntity(request);
        entity.setId(id);
        PessoaFisica atualizada = service.save(entity);
        return ResponseEntity.ok(PessoaFisicaMapper.toResponse(atualizada));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma pessoa física pelo ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
