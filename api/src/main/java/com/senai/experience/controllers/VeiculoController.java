package com.senai.experience.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.VeiculoRequest;
import com.senai.experience.DTO.response.VeiculoResponse;
import com.senai.experience.entities.Veiculo;
import com.senai.experience.mappers.VeiculoMapper;
import com.senai.experience.services.VeiculoService;

@RestController
@RequestMapping("/api/veiculo")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @GetMapping
    public Page<VeiculoResponse> getAllVeiculos(Pageable pageable) {
        return veiculoService.findAll(pageable)
                .map(VeiculoMapper::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponse> getVeiculoById(@PathVariable Long id) {
        Veiculo veiculo = veiculoService.findById(id);
        if (veiculo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(VeiculoMapper.toResponse(veiculo));
    }

    @PostMapping
    public ResponseEntity<VeiculoResponse> createVeiculo(@RequestBody VeiculoRequest dto) {
        Veiculo salvo = veiculoService.save(VeiculoMapper.toEntity(dto));
        return ResponseEntity.status(201).body(VeiculoMapper.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponse> updateVeiculo(@PathVariable Long id, @RequestBody VeiculoRequest dto) {
        Veiculo veiculo = VeiculoMapper.toEntity(dto);
        Veiculo atualizado = veiculoService.update(id, veiculo);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(VeiculoMapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeiculo(@PathVariable Long id) {
        veiculoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chassi/{chassi}")
    public ResponseEntity<VeiculoResponse> getVeiculoByChassi(@PathVariable Integer chassi) {
        Veiculo veiculo = veiculoService.findByChassi(chassi);
        if (veiculo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(VeiculoMapper.toResponse(veiculo));
    }

    /**
     * Chamado pelo Node-RED com o payload bruto do ESP32.
     * POST /api/veiculo/nodered/evento
     *
     * Body (gerado pelo ESP32):
     * {
     *   "chassi":    "CHASSI_00001",
     *   "etapa":     "PINTURA",
     *   "status":    "Iniciado" | "Finalizado",
     *   "timestamp": 12345
     * }
     *
     * Mapeamento etapa → StatusFabricacao (1:1 com o ESP32):
     *   MONTAGEM_ESTRUTURAL  + Iniciado  → MONTAGEM_ESTRUTURAL
     *   PINTURA              + Iniciado  → PINTURA
     *   INSTALACAO_MOTOR     + Iniciado  → INSTALACAO_MOTOR
     *   ACABAMENTO_INTERNO   + Iniciado  → ACABAMENTO_INTERNO
     *   INSPECAO_FINAL       + Iniciado  → INSPECAO_FINAL
     *   LIBERACAO_TRANSPORTE + Finalizado → LIBERACAO_TRANSPORTE
     *
     * O chassi "CHASSI_00001" é convertido para inteiro 1,
     * somado ao offset 10000 para bater com os chassi do banco (10001–10010).
     */
    @PostMapping("/nodered/evento")
    public ResponseEntity<VeiculoResponse> eventoNodeRed(@RequestBody NodeRedEventoRequest body) {
        com.senai.experience.entities.StatusFabricacao novoStatus =
                veiculoService.mapearEtapaParaStatus(body.getEtapa(), body.getStatus());

        if (novoStatus == null) {
            // Etapa/status sem mapeamento relevante — ignora silenciosamente
            return ResponseEntity.ok().build();
        }

        int chassiNum = parseChassi(body.getChassi());
        if (chassiNum <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Veiculo atualizado = veiculoService.atualizarStatus(chassiNum, novoStatus);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(VeiculoMapper.toResponse(atualizado));
    }

    /**
     * Converte "CHASSI_00001" → 10001 (offset 10000 + número extraído).
     * Retorna -1 se o formato for inválido.
     */
    private int parseChassi(String chassiStr) {
        if (chassiStr == null) return -1;
        try {
            // Remove prefixo "CHASSI_" e converte para int
            String numStr = chassiStr.replace("CHASSI_", "").trim();
            int num = Integer.parseInt(numStr);
            return 10000 + num; // CHASSI_00001 → 10001
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @lombok.Data
    static class NodeRedEventoRequest {
        private String chassi;
        private String etapa;
        private String status;
        private Long timestamp;
    }

}
