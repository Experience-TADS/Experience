package com.senai.experience.controllers;

import com.senai.experience.DTO.request.SessaoAnalyticsRequest;
import com.senai.experience.DTO.response.SessaoAnalyticsResponse;
import com.senai.experience.DTO.response.SessaoAnalyticsResumoResponse;
import com.senai.experience.services.SessaoAnalyticsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Analytics", description = "Registro e consulta de sessões de navegação dos clientes no app")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final SessaoAnalyticsService sessaoAnalyticsService;

    @Operation(
        summary = "Registrar sessão de analytics",
        description = "Registra o tempo que um cliente permaneceu em uma seção do aplicativo. Utilizado para análise de comportamento."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Sessão registrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos — clienteId, secao ou duracaoSegundos ausentes/inválidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PostMapping("/sessao")
    public ResponseEntity<SessaoAnalyticsResponse> registrar(@RequestBody @Valid SessaoAnalyticsRequest dto) {
        return ResponseEntity.status(201).body(sessaoAnalyticsService.registrar(dto));
    }

    @Operation(
        summary = "Resumo de analytics por seção",
        description = "Retorna a média de duração e o total de registros agrupados por seção do aplicativo."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resumo retornado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/sessao/resumo")
    public ResponseEntity<List<SessaoAnalyticsResumoResponse>> resumo() {
        return ResponseEntity.ok(sessaoAnalyticsService.resumo());
    }
}
