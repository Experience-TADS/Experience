package com.senai.experience.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senai.experience.entities.Produto;
import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.entities.StatusHistorico;
import com.senai.experience.entities.Veiculo;
import com.senai.experience.repositories.ProdutoRepository;
import com.senai.experience.repositories.StatusHistoricoRepository;
import com.senai.experience.repositories.VeiculoRepository;

import java.time.LocalDateTime;

@Service
public class VeiculoService {
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private StatusHistoricoRepository statusHistoricoRepository;

    public Page<Veiculo> findAll(Pageable pageable) { 
        return veiculoRepository.findAll(pageable);
    }

    public Veiculo findById(Long id) {
        return veiculoRepository.findById(id).orElse(null);
    }

    public Veiculo save(Veiculo veiculo) {
        if (veiculo.getProduto() != null && veiculo.getProduto().getIdProduto() != null) {
            Produto produto = produtoRepository.findById(veiculo.getProduto().getIdProduto())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + veiculo.getProduto().getIdProduto()));
            veiculo.setProduto(produto);
        }
        return veiculoRepository.save(veiculo);
    }

    public Veiculo update(Long id, Veiculo veiculo) {
        Veiculo existing = findById(id);
        if (existing != null) {
            existing.setStatusVeiculo(veiculo.getStatusVeiculo());
            return veiculoRepository.save(existing);
        }
        return null;
    }

    public void delete(Long id) {
        veiculoRepository.deleteById(id);
    }

    public Veiculo findByChassi(int chassi) {
        return veiculoRepository.findByChassi(chassi);
    }

    /**
     * Atualiza o status de um veículo pelo chassi e registra no histórico.
     */
    @Transactional
    public Veiculo atualizarStatus(int chassi, StatusFabricacao novoStatus) {
        Veiculo veiculo = veiculoRepository.findByChassi(chassi);
        if (veiculo == null) return null;

        veiculo.setStatusVeiculo(novoStatus);
        veiculoRepository.save(veiculo);

        StatusHistorico historico = new StatusHistorico();
        historico.setVeiculo(veiculo);
        historico.setStatus(novoStatus);
        historico.setDataAlteracao(LocalDateTime.now());
        statusHistoricoRepository.save(historico);

        return veiculo;
    }

    /**
     * Mapeia a etapa e status do ESP32 para StatusFabricacao do backend.
     * Agora 1:1 — cada etapa do ESP32 tem seu próprio status.
     *
     * Regra: atualiza ao "Iniciado". LIBERACAO_TRANSPORTE atualiza ao "Finalizado".
     * Retorna null quando o evento deve ser ignorado.
     */
    public StatusFabricacao mapearEtapaParaStatus(String etapa, String statusEvento) {
        if (etapa == null || statusEvento == null) return null;

        return switch (etapa.toUpperCase()) {
            case "MONTAGEM_ESTRUTURAL" ->
                "Iniciado".equalsIgnoreCase(statusEvento) ? StatusFabricacao.MONTAGEM_ESTRUTURAL : null;
            case "PINTURA" ->
                "Iniciado".equalsIgnoreCase(statusEvento) ? StatusFabricacao.PINTURA : null;
            case "INSTALACAO_MOTOR" ->
                "Iniciado".equalsIgnoreCase(statusEvento) ? StatusFabricacao.INSTALACAO_MOTOR : null;
            case "ACABAMENTO_INTERNO" ->
                "Iniciado".equalsIgnoreCase(statusEvento) ? StatusFabricacao.ACABAMENTO_INTERNO : null;
            case "INSPECAO_FINAL" ->
                "Iniciado".equalsIgnoreCase(statusEvento) ? StatusFabricacao.INSPECAO_FINAL : null;
            case "LIBERACAO_TRANSPORTE" ->
                "Finalizado".equalsIgnoreCase(statusEvento) ? StatusFabricacao.LIBERACAO_TRANSPORTE : null;
            default -> null;
        };
    }
}
