package com.senai.experience.services;

import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.entities.StatusHistorico;
import com.senai.experience.entities.Veiculo;
import com.senai.experience.repositories.StatusHistoricoRepository;
import com.senai.experience.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusHistoricoService {

    private final StatusHistoricoRepository statusHistoricoRepository;
    private final VeiculoRepository veiculoRepository;
    private final NotificacaoService notificacaoService;

    public List<StatusHistorico> findByVeiculo(Long veiculoId) {
        return statusHistoricoRepository.findByVeiculoIdOrderByDataAlteracaoDesc(veiculoId);
    }

    public StatusHistorico atualizarStatus(Long veiculoId, StatusFabricacao novoStatus) {
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado: " + veiculoId));

        // Ignora silenciosamente se o status já é o mesmo (evita erro em etapas repetidas do ESP32)
        if (veiculo.getStatusVeiculo() == novoStatus) {
            StatusHistorico ultimo = statusHistoricoRepository
                    .findByVeiculoIdOrderByDataAlteracaoDesc(veiculoId)
                    .stream().findFirst().orElse(null);
            return ultimo != null ? ultimo : new StatusHistorico();
        }

        validarTransicao(veiculo.getStatusVeiculo(), novoStatus);

        veiculo.setStatusVeiculo(novoStatus);
        veiculoRepository.save(veiculo);

        StatusHistorico historico = new StatusHistorico();
        historico.setVeiculo(veiculo);
        historico.setStatus(novoStatus);
        historico.setDataAlteracao(LocalDateTime.now());
        StatusHistorico salvo = statusHistoricoRepository.save(historico);

        // Dispara notificação push via Expo — falha silenciosa para não bloquear o fluxo
        notificacaoService.notificarMudancaEtapa(veiculo, novoStatus);

        return salvo;
    }

    private void validarTransicao(StatusFabricacao atual, StatusFabricacao novo) {
        if (atual == null) return;

        boolean valido = switch (atual) {
            case AGUARDANDO            -> novo == StatusFabricacao.MONTAGEM_ESTRUTURAL || novo == StatusFabricacao.CANCELADO;
            case MONTAGEM_ESTRUTURAL   -> novo == StatusFabricacao.PINTURA || novo == StatusFabricacao.CANCELADO;
            case PINTURA               -> novo == StatusFabricacao.INSTALACAO_MOTOR || novo == StatusFabricacao.CANCELADO;
            case INSTALACAO_MOTOR      -> novo == StatusFabricacao.ACABAMENTO_INTERNO || novo == StatusFabricacao.CANCELADO;
            case ACABAMENTO_INTERNO    -> novo == StatusFabricacao.INSPECAO_FINAL || novo == StatusFabricacao.CANCELADO;
            case INSPECAO_FINAL        -> novo == StatusFabricacao.LIBERACAO_TRANSPORTE || novo == StatusFabricacao.CANCELADO;
            case LIBERACAO_TRANSPORTE  -> novo == StatusFabricacao.ENTREGUE;
            case ENTREGUE, CANCELADO   -> false;
        };

        if (!valido) {
            throw new RuntimeException("Transição inválida: " + atual + " → " + novo);
        }
    }
}
