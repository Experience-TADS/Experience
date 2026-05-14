package com.senai.experience.services;

import com.senai.experience.DTO.response.DashboardResponse;
import com.senai.experience.entities.ItemPedido;
import com.senai.experience.entities.Pedido;
import com.senai.experience.entities.Usuario;
import com.senai.experience.entities.Veiculo;
import com.senai.experience.repositories.PedidoRepository;
import com.senai.experience.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public DashboardResponse montarDashboard(String email) {

        // 1. Busca o cliente pelo email extraído do JWT
        Usuario cliente = usuarioRepository.findByEmail(email);
        if (cliente == null) {
            throw new RuntimeException("Cliente não encontrado: " + email);
        }

        // 2. Busca os pedidos do cliente
        List<Pedido> pedidos = pedidoRepository.findByIdCliente(cliente);
        if (pedidos.isEmpty()) {
            throw new RuntimeException("Nenhum pedido encontrado para o cliente: " + email);
        }

        // 3. Pega o pedido mais recente
        Pedido pedidoAtual = pedidos.stream()
                .max(Comparator.comparing(Pedido::getDataPedido))
                .orElseThrow(() -> new RuntimeException("Erro ao localizar pedido"));

        // 4. Navega até o veículo via ItemPedido — sem query manual
        Veiculo veiculo = pedidoAtual.getItens().stream()
                .map(ItemPedido::getVeiculo)
                .filter(v -> v != null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhum veículo vinculado ao pedido"));

        // 5. Data da última atualização de status
        var ultimaAtualizacao = veiculo.getHistorico().stream()
                .max(Comparator.comparing(h -> h.getDataAlteracao()))
                .map(h -> h.getDataAlteracao())
                .orElse(null);

        // 6. Monta e retorna o response — notícias serão adicionadas após US-024
        return new DashboardResponse(
                veiculo.getId(),
                veiculo.getProduto().getModelo(),
                veiculo.getProduto().getCor(),
                veiculo.getProduto().getVersao(),
                veiculo.getStatusVeiculo(),
                ultimaAtualizacao
        );
    }
}
