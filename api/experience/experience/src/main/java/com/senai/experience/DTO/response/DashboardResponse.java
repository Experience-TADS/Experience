package com.senai.experience.DTO.response;

import com.senai.experience.entities.StatusFabricacao;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Resposta agregada do endpoint GET /api/dashboard.
 *
 * <p>Retorna, em uma única requisição, todos os dados que o app mobile precisa
 * para exibir a tela principal do cliente autenticado:</p>
 * <ul>
 *   <li>Dados básicos do usuário</li>
 *   <li>Lista de pedidos com status atual de fabricação</li>
 *   <li>Histórico de etapas do veículo mais recente (timeline)</li>
 * </ul>
 */
@Data
@Builder
public class DashboardResponse {

    // ── Dados do usuário autenticado ─────────────────────────────────────────
    private UsuarioInfo usuario;

    // ── Resumo dos pedidos do cliente ────────────────────────────────────────
    private List<PedidoResumo> pedidos;

    // ── Quantidade total de pedidos ──────────────────────────────────────────
    private int totalPedidos;

    @Data
    @Builder
    public static class UsuarioInfo {
        private Long id;
        private String nome;
        private String email;
        private String role;
    }

    @Data
    @Builder
    public static class PedidoResumo {
        private Long idPedido;
        private LocalDateTime dataPedido;
        private BigDecimal valorTotal;

        // Veículo associado ao pedido (via ItemPedido)
        private Long idVeiculo;
        private String modeloVeiculo;
        private String corVeiculo;
        private Integer chassiVeiculo;

        // Status atual de fabricação
        private StatusFabricacao statusAtual;
        private String statusDescricao;
        private LocalDateTime dataUltimaAtualizacao;

        // Percentual de progresso na linha de fabricação (0–100)
        private int progressoPercent;

        // Histórico de etapas (timeline para o app)
        private List<EtapaHistorico> historico;
    }

    @Data
    @Builder
    public static class EtapaHistorico {
        private StatusFabricacao status;
        private String descricao;
        private LocalDateTime dataAlteracao;
        private boolean concluida;
    }
}
