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

import com.senai.experience.DTO.request.VeiculoRequest;
import com.senai.experience.DTO.response.VeiculoResponse;
import com.senai.experience.entities.Veiculo;
import com.senai.experience.mappers.VeiculoMapper;
import com.senai.experience.services.VeiculoService;
import lombok.RequiredArgsConstructor;

@Tag(name = "Veículos", description = "Gerenciamento de veículos em fabricação e integração com Node-RED/ESP32")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/veiculo")
public class VeiculoController {

    private final VeiculoService veiculoService;

    @Operation(summary = "Listar veículos", description = "Retorna todos os veículos cadastrados de forma paginada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    public Page<VeiculoResponse> getAllVeiculos(Pageable pageable) {
        return veiculoService.findAll(pageable)
                .map(VeiculoMapper::toResponse);
    }

    @Operation(summary = "Buscar veículo por ID", description = "Retorna os dados de um veículo específico pelo seu identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Veículo encontrado"),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponse> getVeiculoById(
            @Parameter(description = "ID do veículo", example = "1") @PathVariable Long id) {
        Veiculo veiculo = veiculoService.findById(id);
        if (veiculo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(VeiculoMapper.toResponse(veiculo));
    }

    @Operation(summary = "Cadastrar veículo", description = "Registra um novo veículo no sistema vinculado a um produto e pedido.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Veículo criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "Chassi já cadastrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<VeiculoResponse> createVeiculo(@Valid @RequestBody VeiculoRequest dto) {
        Veiculo salvo = veiculoService.save(VeiculoMapper.toEntity(dto));
        return ResponseEntity.status(201).body(VeiculoMapper.toResponse(salvo));
    }

    @Operation(summary = "Atualizar veículo", description = "Atualiza o status de fabricação de um veículo existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponse> updateVeiculo(
            @Parameter(description = "ID do veículo", example = "1") @PathVariable Long id,
            @RequestBody VeiculoRequest dto) {
        Veiculo veiculo = VeiculoMapper.toEntity(dto);
        Veiculo atualizado = veiculoService.update(id, veiculo);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(VeiculoMapper.toResponse(atualizado));
    }

    @Operation(summary = "Remover veículo", description = "Remove um veículo pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Veículo removido com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeiculo(
            @Parameter(description = "ID do veículo", example = "1") @PathVariable Long id) {
        veiculoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar veículo por chassi", description = "Retorna o veículo cujo número de chassi corresponde ao informado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Veículo encontrado"),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/chassi/{chassi}")
    public ResponseEntity<VeiculoResponse> getVeiculoByChassi(
            @Parameter(description = "Número do chassi", example = "10001") @PathVariable Integer chassi) {
        Veiculo veiculo = veiculoService.findByChassi(chassi);
        if (veiculo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(VeiculoMapper.toResponse(veiculo));
    }

    @PostMapping("/{id}/confirmacao-chegada")
    public ResponseEntity<VeiculoResponse> confirmarChegada(
            @Parameter(description = "ID do veículo", example = "1") @PathVariable Long id) {
        Veiculo veiculo = veiculoService.confirmarChegada(id);
        if (veiculo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(VeiculoMapper.toResponse(veiculo));
    }
}
