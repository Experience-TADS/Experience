package com.senai.experience.services;

import com.senai.experience.DTO.response.DashboardResponse;
import com.senai.experience.entities.*;
import com.senai.experience.repositories.PedidoRepository;
import com.senai.experience.repositories.StatusHistoricoRepository;
import com.senai.experience.repositories.UsuarioRepository;
import com.senai.experience.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço responsável por agregar os dados do dashboard do cliente.
 *
 * <p>Produz a resposta do {@code GET /api/dashboard} em uma única consulta
 * combinada, evitando múltiplas requisições do app mobile.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    // Descrições amigáveis para exibição no app
    private static final Map<StatusFabricacao, String> DESCRICOES = Map.of(
            StatusFabricacao.AGUARDANDO,            "Aguardando início da fabricação",
            StatusFabricacao.MONTAGEM_ESTRUTURAL,   "Montagem estrutural em andamento",
            StatusFabricacao.PINTURA,               "Pintura em andamento",
            StatusFabricacao.INSTALACAO_MOTOR,      "Instalação do motor",
            StatusFabricacao.ACABAMENTO_INTERNO,    "Acabamento interno",
            StatusFabricacao.INSPECAO_FINAL,        "Inspeção final de qualidade",
            StatusFabricacao.LIBERACAO_TRANSPORTE,  "Liberado para transporte",
            StatusFabricacao.ENTREGUE,              "Veículo entregue",
            StatusFabricacao.CANCELADO,             "Pedido cancelado"
    );

    // Ordem das etapas para cálculo de progresso (0–100%)
    private static final List<StatusFabricacao> ORDEM_ETAPAS = List.of(
            StatusFabricacao.AGUARDANDO,
            StatusFabricacao.MONTAGEM_ESTRUTURAL,
            StatusFabricacao.PINTURA,
            StatusFabricacao.INSTALACAO_MOTOR,
            StatusFabricacao.ACABAMENTO_INTERNO,
            StatusFabricacao.INSPECAO_FINAL,
            StatusFabricacao.LIBERACAO_TRANSPORTE,
            StatusFabricacao.ENTREGUE
    );

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final VeiculoRepository veiculoRepository;
    private final StatusHistoricoRepository statusHistoricoRepository;

    /**
     * Retorna o dashboard completo do cliente autenticado.
     *
     * @param email email extraído do JWT pelo Spring Security
     * @return {@link DashboardResponse} com dados do usuário, pedidos e histórico
     */
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado: " + email);
        }

        List<Pedido> pedidos = pedidoRepository.findByIdCliente(usuario);
        pedidos.forEach(p -> p.getItens().size()); // força carregamento lazy dentro da transação

        List<DashboardResponse.PedidoResumo> resumos = pedidos.stream()
                .map(pedido -> construirResumoPedido(pedido, usuario))
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .usuario(DashboardResponse.UsuarioInfo.builder()
                        .id(usuario.getId())
                        .nome(usuario.getNome())
                        .email(usuario.getEmail())
                        .role(usuario.getRole() != null ? usuario.getRole().name() : "ROLE_CLIENTE")
                        .build())
                .pedidos(resumos)
                .totalPedidos(resumos.size())
                .build();
    }

    private DashboardResponse.PedidoResumo construirResumoPedido(Pedido pedido, Usuario usuario) {
        // Busca o veículo vinculado ao pedido (via idPedido na tabela de veículos)
        Veiculo veiculo = veiculoRepository.findByIdPedido(pedido.getId()).orElse(null);

        StatusFabricacao statusAtual = veiculo != null ? veiculo.getStatusVeiculo() : StatusFabricacao.AGUARDANDO;
        String descricao = DESCRICOES.getOrDefault(statusAtual, statusAtual.name());
        int progresso = calcularProgresso(statusAtual);

        // Histórico do veículo para exibição como timeline no app
        List<DashboardResponse.EtapaHistorico> historico = List.of();
        java.time.LocalDateTime dataUltimaAtualizacao = pedido.getDataPedido();

        if (veiculo != null) {
            List<StatusHistorico> hist = statusHistoricoRepository
                    .findByVeiculoIdOrderByDataAlteracaoDesc(veiculo.getId());

            historico = ORDEM_ETAPAS.stream()
                    .map(etapa -> {
                        // Verifica se essa etapa já foi registrada no histórico
                        StatusHistorico registro = hist.stream()
                                .filter(h -> h.getStatus() == etapa)
                                .findFirst().orElse(null);

                        boolean concluida = registro != null ||
                                ORDEM_ETAPAS.indexOf(etapa) < ORDEM_ETAPAS.indexOf(statusAtual);

                        return DashboardResponse.EtapaHistorico.builder()
                                .status(etapa)
                                .descricao(DESCRICOES.getOrDefault(etapa, etapa.name()))
                                .dataAlteracao(registro != null ? registro.getDataAlteracao() : null)
                                .concluida(concluida)
                                .build();
                    })
                    .collect(Collectors.toList());

            dataUltimaAtualizacao = hist.stream()
                    .map(StatusHistorico::getDataAlteracao)
                    .max(Comparator.naturalOrder())
                    .orElse(pedido.getDataPedido());
        }

        var builder = DashboardResponse.PedidoResumo.builder()
                .idPedido(pedido.getId())
                .dataPedido(pedido.getDataPedido())
                .valorTotal(pedido.getValorTotal())
                .statusAtual(statusAtual)
                .statusDescricao(descricao)
                .dataUltimaAtualizacao(dataUltimaAtualizacao)
                .progressoPercent(progresso)
                .historico(historico);

        if (veiculo != null) {
            builder
                .idVeiculo(veiculo.getId())
                .chassiVeiculo(veiculo.getChassi());

            if (veiculo.getProduto() != null) {
                builder
                    .modeloVeiculo(veiculo.getProduto().getModelo())
                    .corVeiculo(veiculo.getProduto().getCor());
            }
        }

        return builder.build();
    }

    /**
     * Calcula o percentual de progresso com base na etapa atual.
     * AGUARDANDO = 0%, ENTREGUE = 100%.
     */
    private int calcularProgresso(StatusFabricacao status) {
        if (status == StatusFabricacao.CANCELADO) return 0;
        int idx = ORDEM_ETAPAS.indexOf(status);
        if (idx < 0) return 0;
        return (int) Math.round((idx * 100.0) / (ORDEM_ETAPAS.size() - 1));
    }
}
