package com.senai.experience.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.senai.experience.DTO.PedidoRequest;
import com.senai.experience.entities.Pedido;
import com.senai.experience.entities.Usuario;
import com.senai.experience.repositories.PedidoRepository;
import com.senai.experience.repositories.UsuarioRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Pedido findById(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    public Pedido save(PedidoRequest request) {
        Usuario cliente = usuarioRepository.findById(request.getIdCliente()).orElseThrow(
            () -> new RuntimeException("Cliente não encontrado: " + request.getIdCliente()));
        Usuario vendedor = usuarioRepository.findById(request.getIdVendedor()).orElseThrow(
            () -> new RuntimeException("Vendedor não encontrado: " + request.getIdVendedor()));

        Pedido pedido = new Pedido();
        pedido.setIdCliente(cliente);
        pedido.setIdVendedor(vendedor);
        pedido.setDataPedido(request.getDataPedido());
        pedido.setValorTotal(request.getValorTotal());
        return pedidoRepository.save(pedido);
    }

    public Pedido update(Long id, PedidoRequest request) {
        Pedido existing = findById(id);
        if (existing == null) return null;

        Usuario cliente = usuarioRepository.findById(request.getIdCliente()).orElseThrow(
            () -> new RuntimeException("Cliente não encontrado: " + request.getIdCliente()));
        Usuario vendedor = usuarioRepository.findById(request.getIdVendedor()).orElseThrow(
            () -> new RuntimeException("Vendedor não encontrado: " + request.getIdVendedor()));

        existing.setIdCliente(cliente);
        existing.setIdVendedor(vendedor);
        existing.setDataPedido(request.getDataPedido());
        existing.setValorTotal(request.getValorTotal());
        return pedidoRepository.save(existing);
    }

    public void delete(Long id) {
        pedidoRepository.deleteById(id);
    }
}
