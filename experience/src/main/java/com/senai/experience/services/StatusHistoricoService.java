package com.senai.experience.services;

import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.entities.StatusHistorico;
import com.senai.experience.entities.Veiculo;
import com.senai.experience.repositories.StatusHistoricoRepository;
import com.senai.experience.repositories.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatusHistoricoService {

    @Autowired
    private StatusHistoricoRepository statusHistoricoRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    public List<StatusHistorico> findByVeiculo(Long veiculoId) {
        return statusHistoricoRepository.findByVeiculoIdOrderByDataAlteracaoDesc(veiculoId);
    }

    public StatusHistorico atualizarStatus(Long veiculoId, StatusFabricacao novoStatus) {
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado: " + veiculoId));

        validarTransicao(veiculo.getStatusVeiculo(), novoStatus);

        veiculo.setStatusVeiculo(novoStatus);
        veiculoRepository.save(veiculo);

        StatusHistorico historico = new StatusHistorico();
        historico.setVeiculo(veiculo);
        historico.setStatus(novoStatus);
        historico.setDataAlteracao(LocalDateTime.now());
        return statusHistoricoRepository.save(historico);
    }

    private void validarTransicao(StatusFabricacao atual, StatusFabricacao novo) {
        if (atual == null) return;

        boolean valido = switch (atual) {
            case AGUARDANDO         -> novo == StatusFabricacao.EM_FABRICACAO || novo == StatusFabricacao.CANCELADO;
            case EM_FABRICACAO      -> novo == StatusFabricacao.PINTURA || novo == StatusFabricacao.CANCELADO;
            case PINTURA            -> novo == StatusFabricacao.CONTROLE_QUALIDADE || novo == StatusFabricacao.CANCELADO;
            case CONTROLE_QUALIDADE -> novo == StatusFabricacao.CONCLUIDO || novo == StatusFabricacao.CANCELADO;
            case CONCLUIDO          -> novo == StatusFabricacao.ENTREGUE;
            case ENTREGUE, CANCELADO -> false;
        };

        if (!valido) {
            throw new RuntimeException("Transição inválida: " + atual + " → " + novo);
        }
    }
}
