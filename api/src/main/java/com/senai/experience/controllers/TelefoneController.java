package com.senai.experience.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.TelefoneRequest;
import com.senai.experience.DTO.response.TelefoneResponse;
import com.senai.experience.entities.Telefone;
import com.senai.experience.mappers.TelefoneMapper;
import com.senai.experience.services.TelefoneService;
import lombok.RequiredArgsConstructor;

@Tag(name = "Telefones", description = "Gerenciamento de telefones dos usuários")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/telefones")
public class TelefoneController {

    private final TelefoneService telefoneService;

    @Operation(summary = "Listar telefones", description = "Retorna todos os telefones cadastrados de forma paginada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<Telefone>> getAllTelefones(Pageable pageable) {
        return ResponseEntity.ok(telefoneService.findAll(pageable));
    }

    @Operation(summary = "Buscar telefone por ID", description = "Retorna um telefone específico pelo seu identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Telefone encontrado"),
        @ApiResponse(responseCode = "404", description = "Telefone não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TelefoneResponse> getTelefoneById(
            @Parameter(description = "ID do telefone", example = "1") @PathVariable Long id) {
        Telefone telefone = telefoneService.findById(id);
        if (telefone != null) {
            return ResponseEntity.ok(TelefoneMapper.toResponse(telefone));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Cadastrar telefone", description = "Registra um novo telefone vinculado a um usuário. Aceita 10 dígitos (fixo) ou 11 dígitos (celular com nono dígito).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Telefone criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Número inválido — deve ter 10 ou 11 dígitos numéricos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TelefoneResponse> createTelefone(@RequestBody @Valid TelefoneRequest dto) {
        Telefone telefone = TelefoneMapper.toEntity(dto);
        Telefone novoTelefone = telefoneService.save(telefone);
        return ResponseEntity.status(201).body(TelefoneMapper.toResponse(novoTelefone));
    }

    @Operation(summary = "Atualizar telefone", description = "Atualiza o número de um telefone existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Telefone atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Número inválido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Telefone não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TelefoneResponse> updateTelefone(
            @Parameter(description = "ID do telefone", example = "1") @PathVariable Long id,
            @RequestBody @Valid TelefoneRequest dto) {
        Telefone telefone = TelefoneMapper.toEntity(dto);
        Telefone atualizado = telefoneService.update(id, telefone);
        if (atualizado != null) {
            return ResponseEntity.ok(TelefoneMapper.toResponse(atualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Remover telefone", description = "Remove um telefone pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Telefone removido com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Telefone não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTelefone(
            @Parameter(description = "ID do telefone", example = "1") @PathVariable Long id) {
        telefoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
