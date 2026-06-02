package com.senai.experience.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senai.experience.DTO.request.PedidoRequest;
import com.senai.experience.entities.Pedido;
import com.senai.experience.entities.Usuario;
import com.senai.experience.entities.role.UserRole;
import com.senai.experience.repositories.PedidoRepository;
import com.senai.experience.repositories.UsuarioRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Page<Pedido> findAll(Pageable pageable) {
        Page<Pedido> page = pedidoRepository.findAll(pageable);
        // Força carregamento dos itens (lazy) dentro da transação
        page.forEach(p -> p.getItens().size());
        return page;
    }

    @Transactional(readOnly = true)
    public Pedido findById(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null) {
            pedido.getItens().size();
        }
        return pedido;
    }

    public Pedido save(PedidoRequest request) {
        Usuario cliente = usuarioRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + request.getIdCliente()));
        Usuario vendedor = usuarioRepository.findById(request.getIdVendedor())
                .orElseThrow(() -> new RuntimeException("Vendedor não encontrado: " + request.getIdVendedor()));

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

        Usuario cliente = usuarioRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + request.getIdCliente()));
        Usuario vendedor = usuarioRepository.findById(request.getIdVendedor())
                .orElseThrow(() -> new RuntimeException("Vendedor não encontrado: " + request.getIdVendedor()));

        existing.setIdCliente(cliente);
        existing.setIdVendedor(vendedor);
        existing.setDataPedido(request.getDataPedido());
        existing.setValorTotal(request.getValorTotal());
        return pedidoRepository.save(existing);
    }

    public void delete(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Pedido> findMeusPedidos(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null || usuario.getRole() == null) return List.of();

        List<Pedido> pedidos;
        if (usuario.getRole() == UserRole.ADMIN) {
            pedidos = pedidoRepository.findAll();
        } else if (usuario.getRole() == UserRole.VENDEDOR) {
            pedidos = pedidoRepository.findByIdVendedor(usuario);
        } else {
            pedidos = pedidoRepository.findByIdCliente(usuario);
        }

        // Força carregamento dos itens dentro da transação
        pedidos.forEach(p -> p.getItens().size());
        return pedidos;
    }
}
