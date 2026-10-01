package com.senai.experience.controllers;

import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.entities.StatusHistorico;
import com.senai.experience.services.StatusHistoricoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Histórico de Status", description = "Consulta e atualização do histórico de etapas de fabricação de um veículo")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/veiculo/{veiculoId}/status")
public class StatusHistoricoController {

    private final StatusHistoricoService statusHistoricoService;

    @Operation(
        summary = "Listar histórico de status",
        description = "Retorna todas as transições de status registradas para um veículo específico, em ordem cronológica."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<StatusHistorico>> getHistorico(
            @Parameter(description = "ID do veículo", example = "1") @PathVariable Long veiculoId) {
        return ResponseEntity.ok(statusHistoricoService.findByVeiculo(veiculoId));
    }

    @Operation(
        summary = "Atualizar status do veículo",
        description = "Registra uma nova transição de status para o veículo. O novo status deve respeitar a sequência de fabricação definida."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status atualizado e histórico registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Transição de status inválida", content = @Content),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<StatusHistorico> atualizarStatus(
            @Parameter(description = "ID do veículo", example = "1") @PathVariable Long veiculoId,
            @RequestBody StatusFabricacao novoStatus) {
        return ResponseEntity.ok(statusHistoricoService.atualizarStatus(veiculoId, novoStatus));
    }
}
