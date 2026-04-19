package com.senai.experience.controllers;

import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.entities.StatusHistorico;
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
