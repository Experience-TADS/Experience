     package com.senai.experience.controllers;

import com.senai.experience.DTO.response.StatusHistoricoResponse;
import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.mappers.StatusHistoricoMapper;
import com.senai.experience.services.StatusHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veiculo/{veiculoId}/status")
public class StatusHistoricoController {

    @Autowired
    private StatusHistoricoService statusHistoricoService;

    @GetMapping
    public ResponseEntity<List<StatusHistoricoResponse>> getHistorico(@PathVariable Long veiculoId) {
        List<StatusHistoricoResponse> historico = statusHistoricoService.findByVeiculo(veiculoId)
            .stream()
            .map(StatusHistoricoMapper::toResponse)
            .toList();
            return ResponseEntity.ok(historico);
    }

    @PostMapping
    public ResponseEntity<StatusHistoricoResponse> atualizarStatus(
            @PathVariable Long veiculoId,
            @RequestBody StatusFabricacao novoStatus) {
        return ResponseEntity.ok(
            StatusHistoricoMapper.toResponse(
                statusHistoricoService.atualizarStatus(veiculoId, novoStatus)));
    }
}
