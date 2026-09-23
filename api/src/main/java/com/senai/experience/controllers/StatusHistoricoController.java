package com.senai.experience.controllers;

import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.entities.StatusHistorico;
import com.senai.experience.services.StatusHistoricoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/veiculo/{veiculoId}/status")
public class StatusHistoricoController {

    private final StatusHistoricoService statusHistoricoService;

    @GetMapping
    public ResponseEntity<List<StatusHistorico>> getHistorico(@PathVariable Long veiculoId) {
        return ResponseEntity.ok(statusHistoricoService.findByVeiculo(veiculoId));
    }

    @PostMapping
    public ResponseEntity<StatusHistorico> atualizarStatus(
            @PathVariable Long veiculoId,
            @RequestBody StatusFabricacao novoStatus) {
        return ResponseEntity.ok(statusHistoricoService.atualizarStatus(veiculoId, novoStatus));
    }
}
