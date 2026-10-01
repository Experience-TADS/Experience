package com.senai.experience.controllers;

import com.senai.experience.DTO.request.PessoaJuridicaRequest;
import com.senai.experience.DTO.response.PessoaJuridicaResponse;
import com.senai.experience.entities.PessoaJuridica;
import com.senai.experience.mappers.PessoaJuridicaMapper;
import com.senai.experience.services.PessoaJuridicaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pessoaJuridica")
@Tag(name = "Pessoa Jurídica", description = "Gerenciamento de pessoas jurídicas")
public class PessoaJuridicaController {

    private final PessoaJuridicaService service;

    public PessoaJuridicaController(PessoaJuridicaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todas as pessoas jurídicas paginadas")
    public Page<PessoaJuridicaResponse> getAll(Pageable pageable) {
        return service.findAll(pageable).map(PessoaJuridicaMapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma pessoa jurídica pelo ID")
    public ResponseEntity<PessoaJuridicaResponse> getById(@PathVariable Long id) {
        PessoaJuridica pessoaJuridica = service.findById(id);
        if (pessoaJuridica == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PessoaJuridicaMapper.toResponse(pessoaJuridica));
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova pessoa jurídica")
    public ResponseEntity<PessoaJuridicaResponse> create(@Valid @RequestBody PessoaJuridicaRequest request) {
        PessoaJuridica entity = PessoaJuridicaMapper.toEntity(request);
        PessoaJuridica salva = service.save(entity);
        return ResponseEntity.status(201).body(PessoaJuridicaMapper.toResponse(salva));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados de uma pessoa jurídica")
    public ResponseEntity<PessoaJuridicaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PessoaJuridicaRequest request) {

        PessoaJuridica existing = service.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        PessoaJuridica entity = PessoaJuridicaMapper.toEntity(request);
        entity.setId(id);
        PessoaJuridica atualizada = service.save(entity);
        return ResponseEntity.ok(PessoaJuridicaMapper.toResponse(atualizada));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma pessoa jurídica pelo ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
