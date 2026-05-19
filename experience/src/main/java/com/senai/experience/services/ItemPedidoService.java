package com.senai.experience.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.senai.experience.entities.ItemPedido;
import com.senai.experience.entities.Pedido;
import com.senai.experience.entities.Produto;
import com.senai.experience.repositories.ItemPedidoRepository;
import com.senai.experience.repositories.PedidoRepository;
import com.senai.experience.repositories.ProdutoRepository;

@Service
public class ItemPedidoService {
    @Autowired
    private ItemPedidoRepository repository;
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;

    public ItemPedido save(ItemPedido item) {
        if (item.getPedido() != null && item.getPedido().getId() != null) {
            Pedido pedido = pedidoRepository.findById(item.getPedido().getId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
            item.setPedido(pedido);
        }
        
        if (item.getProduto() != null && item.getProduto().getIdProduto() != null) {
            Produto produto = produtoRepository.findById(item.getProduto().getIdProduto())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            item.setProduto(produto);
        }
        
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
            return save(item);
        }
        return null;
    }
}
