package com.senai.experience.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.EnderecoRequest;
import com.senai.experience.DTO.response.EnderecoResponse;
import com.senai.experience.mappers.EnderecoMapper;
import com.senai.experience.entities.Endereco;
import com.senai.experience.services.EnderecoService;
import lombok.RequiredArgsConstructor;

@Tag(name = "Endereços", description = "Gerenciamento de endereços dos usuários")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/endereco")
public class EnderecoController {

    private final EnderecoService enderecoService;

    @Operation(summary = "Listar endereços", description = "Retorna todos os endereços cadastrados de forma paginada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<Endereco>> getAllEnderecos(Pageable pageable) {
        return ResponseEntity.ok(enderecoService.findAll(pageable));
    }

    @Operation(summary = "Buscar endereço por ID", description = "Retorna um endereço específico pelo seu identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
        @ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponse> getEnderecoById(
            @Parameter(description = "ID do endereço", example = "1") @PathVariable Long id) {
        Endereco endereco = enderecoService.findById(id);
        if (endereco != null) {
            return ResponseEntity.ok(EnderecoMapper.toResponse(endereco));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Cadastrar endereço", description = "Registra um novo endereço vinculado a um usuário. CEP aceito nos formatos 00000000 e 00000-000.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos — CEP, logradouro, bairro ou cidade inválidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EnderecoResponse> createEndereco(@RequestBody @Valid EnderecoRequest dto) {
        Endereco endereco = EnderecoMapper.toEntity(dto);
        Endereco salvo = enderecoService.save(endereco);
        return ResponseEntity.status(201).body(EnderecoMapper.toResponse(salvo));
    }

    @Operation(summary = "Atualizar endereço", description = "Atualiza os dados de um endereço existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponse> updateEndereco(@PathVariable Long id, @RequestBody @Valid EnderecoRequest dto) {
        Endereco endereco = EnderecoMapper.toEntity(dto);
        Endereco atualizado = enderecoService.update(id, endereco);
        if (atualizado != null) {
            return ResponseEntity.ok(EnderecoMapper.toResponse(atualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Remover endereço", description = "Remove um endereço pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Endereço removido com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Endereço não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEndereco(
            @Parameter(description = "ID do endereço", example = "1") @PathVariable Long id) {
        enderecoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
