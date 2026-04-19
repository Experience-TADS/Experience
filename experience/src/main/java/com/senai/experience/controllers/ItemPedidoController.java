package com.senai.experience.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.ItemPedidoRequest;
import com.senai.experience.DTO.response.ItemPedidoResponse;
import com.senai.experience.entities.ItemPedido;
import com.senai.experience.mappers.ItemPedidoMapper;
import com.senai.experience.services.ItemPedidoService;

@RestController
@RequestMapping("/api/itens-pedido")
public class ItemPedidoController {

    @Autowired
    private ItemPedidoService service;

    @GetMapping
    public List<ItemPedidoResponse> getAllItemPedidos() {
        return service.listarTodos()
                .stream()
                .map(ItemPedidoMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPedidoResponse> buscarPorId(@PathVariable Long id) {
        ItemPedido item = service.findById(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ItemPedidoMapper.toResponse(item));
    }

    @PostMapping
    public ResponseEntity<ItemPedidoResponse> createItemPedido(@RequestBody ItemPedidoRequest dto) {
        ItemPedido salvo = service.save(ItemPedidoMapper.toEntity(dto));
        return ResponseEntity.status(201).body(ItemPedidoMapper.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemPedidoResponse> updateItemPedido(@PathVariable Long id, @RequestBody ItemPedidoRequest dto) {
        ItemPedido item = ItemPedidoMapper.toEntity(dto);
        item.setIdItemPedido(id);
        ItemPedido atualizado = service.update(item);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ItemPedidoMapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
