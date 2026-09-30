package com.senai.experience.controllers;

import com.senai.experience.entities.PessoaFisica;
import com.senai.experience.services.PessoaFisicaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pessoa Física", description = "Gerenciamento de clientes e usuários do tipo pessoa física (CPF)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/pessoaFisica")
public class PessoaFisicaController {

    private final PessoaFisicaService service;

    public PessoaFisicaController(PessoaFisicaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar pessoas físicas", description = "Retorna todas as pessoas físicas cadastradas de forma paginada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    public Page<PessoaFisica> getAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @Operation(summary = "Buscar pessoa física por ID", description = "Retorna os dados de uma pessoa física pelo seu identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa física encontrada"),
        @ApiResponse(responseCode = "404", description = "Não encontrada", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PessoaFisica> getById(
            @Parameter(description = "ID da pessoa física", example = "1") @PathVariable Long id) {
        PessoaFisica pessoaFisica = service.findById(id);
        if (pessoaFisica != null) {
            return ResponseEntity.ok(pessoaFisica);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Cadastrar pessoa física", description = "Cria um novo registro de pessoa física com CPF.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa física criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PostMapping
    public PessoaFisica create(@RequestBody PessoaFisica pessoaFisica) {
        return service.save(pessoaFisica);
    }

    @Operation(summary = "Remover pessoa física", description = "Remove o registro de uma pessoa física pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Removida com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da pessoa física", example = "1") @PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar pessoa física", description = "Atualiza os dados de uma pessoa física existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Não encontrada", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PessoaFisica> update(
            @Parameter(description = "ID da pessoa física", example = "1") @PathVariable Long id,
            @RequestBody PessoaFisica pessoaFisica) {
        PessoaFisica existingPessoaFisica = service.findById(id);
        if (existingPessoaFisica != null) {
            pessoaFisica.setId(id);
            return ResponseEntity.ok(service.save(pessoaFisica));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
