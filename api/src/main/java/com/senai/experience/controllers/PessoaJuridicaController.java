package com.senai.experience.controllers;

import com.senai.experience.DTO.request.PessoaJuridicaRequest;
import com.senai.experience.DTO.response.PessoaJuridicaResponse;
import com.senai.experience.entities.PessoaJuridica;
import com.senai.experience.mappers.PessoaJuridicaMapper;
import com.senai.experience.services.PessoaJuridicaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pessoa Jurídica", description = "Gerenciamento de empresas e usuários do tipo pessoa jurídica (CNPJ)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/pessoaJuridica")
public class PessoaJuridicaController {

    private final PessoaJuridicaService service;

    public PessoaJuridicaController(PessoaJuridicaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar pessoas jurídicas", description = "Retorna todas as pessoas jurídicas cadastradas de forma paginada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    public Page<PessoaJuridicaResponse> getAll(Pageable pageable) {
        return service.findAll(pageable).map(PessoaJuridicaMapper::toResponse);
    }

    @Operation(summary = "Buscar pessoa jurídica por ID", description = "Retorna os dados de uma pessoa jurídica pelo seu identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa jurídica encontrada"),
        @ApiResponse(responseCode = "404", description = "Não encontrada", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PessoaJuridicaResponse> getById(
            @Parameter(description = "ID da pessoa jurídica", example = "1") @PathVariable Long id) {
        PessoaJuridica pessoaJuridica = service.findById(id);
        if (pessoaJuridica == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PessoaJuridicaMapper.toResponse(pessoaJuridica));
    }

    @Operation(summary = "Cadastrar pessoa jurídica", description = "Cria um novo registro de pessoa jurídica com CNPJ e razão social.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa jurídica criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PessoaJuridicaResponse> create(@Valid @RequestBody PessoaJuridicaRequest request) {
        PessoaJuridica entity = PessoaJuridicaMapper.toEntity(request);
        PessoaJuridica salva = service.save(entity);
        return ResponseEntity.status(201).body(PessoaJuridicaMapper.toResponse(salva));
    }

    @Operation(summary = "Remover pessoa jurídica", description = "Remove o registro de uma pessoa jurídica pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Removida com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da pessoa jurídica", example = "1") @PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar pessoa jurídica", description = "Atualiza os dados de uma pessoa jurídica existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Não encontrada", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PessoaJuridica> update(
            @Parameter(description = "ID da pessoa jurídica", example = "1") @PathVariable Long id,
            @RequestBody PessoaJuridica pessoaJuridica) {
        PessoaJuridica existingPessoaJuridica = service.findById(id);
        if (existingPessoaJuridica != null) {
            pessoaJuridica.setId(id);
            return ResponseEntity.ok(service.save(pessoaJuridica));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
