package com.senai.experience.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.senai.experience.entities.Pedido;
import com.senai.experience.repositories.PedidoRepository;
@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> findAll() { 
        return pedidoRepository.findAll();
    }

    public Pedido findById(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    public Pedido save(Pedido pedido) {
        pedido.setDataPedido(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    public Pedido update(Long id, Pedido pedido) {
        Pedido existing = findById(id);
        if (existing != null) {
            existing.setDataPedido(pedido.getDataPedido());
            existing.setValorTotal(pedido.getValorTotal());
            return pedidoRepository.save(existing);
        }
        return null;
    }

    public void delete(Long id) {
        pedidoRepository.deleteById(id);
    }
}
