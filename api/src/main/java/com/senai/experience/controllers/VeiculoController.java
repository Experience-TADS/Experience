package com.senai.experience.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    public ResponseEntity<VeiculoResponse> createVeiculo(@RequestBody VeiculoRequest dto) {
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

    @Operation(
        summary = "Evento Node-RED (ESP32)",
        description = """
            Endpoint público consumido pelo Node-RED com o payload bruto gerado pelo ESP32.
            Converte a etapa e status do evento para o StatusFabricacao correspondente e
            atualiza o veículo cujo chassi for identificado.

            Mapeamento de etapas:
            - MONTAGEM_ESTRUTURAL + Iniciado → MONTAGEM_ESTRUTURAL
            - PINTURA + Iniciado → PINTURA
            - INSTALACAO_MOTOR + Iniciado → INSTALACAO_MOTOR
            - ACABAMENTO_INTERNO + Iniciado → ACABAMENTO_INTERNO
            - INSPECAO_FINAL + Iniciado → INSPECAO_FINAL
            - LIBERACAO_TRANSPORTE + Finalizado → LIBERACAO_TRANSPORTE

            O chassi "CHASSI_00001" é convertido para o inteiro 10001 (offset 10000 + número).
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evento processado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Formato de chassi inválido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado", content = @Content)
    })
    @io.swagger.v3.oas.annotations.security.SecurityRequirements
    @PostMapping("/nodered/evento")
    public ResponseEntity<VeiculoResponse> eventoNodeRed(@RequestBody NodeRedEventoRequest body) {
        com.senai.experience.entities.StatusFabricacao novoStatus =
                veiculoService.mapearEtapaParaStatus(body.getEtapa(), body.getStatus());

        if (novoStatus == null) {
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

    private int parseChassi(String chassiStr) {
        if (chassiStr == null) return -1;
        try {
            String numStr = chassiStr.replace("CHASSI_", "").trim();
            int num = Integer.parseInt(numStr);
            return 10000 + num;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @io.swagger.v3.oas.annotations.media.Schema(description = "Payload enviado pelo Node-RED com dados brutos do ESP32")
    @lombok.Data
    static class NodeRedEventoRequest {
        @io.swagger.v3.oas.annotations.media.Schema(description = "Identificador do chassi no formato CHASSI_XXXXX", example = "CHASSI_00001")
        private String chassi;
        @io.swagger.v3.oas.annotations.media.Schema(description = "Etapa de fabricação", example = "PINTURA")
        private String etapa;
        @io.swagger.v3.oas.annotations.media.Schema(description = "Status do evento", example = "Iniciado", allowableValues = {"Iniciado", "Finalizado"})
        private String status;
        @io.swagger.v3.oas.annotations.media.Schema(description = "Timestamp Unix do evento", example = "1700000000")
        private Long timestamp;
    }

    @Operation(summary = "Confirmar chegada na concessionária", description = "Marca o veículo como NA_CONCESSIONARIA e registra no histórico de status.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Chegada confirmada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
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
