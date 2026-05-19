package com.senai.experience.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.senai.experience.entities.ItemPedido;
import com.senai.experience.repositories.ItemPedidoRepository;

@Service

public class ItemPedidoService {
    @Autowired
    private ItemPedidoRepository repository;

    public ItemPedido save(ItemPedido item) {
        return repository.save(item);
    }

    public Page<ItemPedido> listarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public ItemPedido findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public ItemPedido update(ItemPedido item) {
        if (item.getIdItemPedido() != null && repository.existsById(item.getIdItemPedido())) {
            return repository.save(item);
        }
        return null;
    }


}
