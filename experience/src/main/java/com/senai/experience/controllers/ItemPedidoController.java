package com.senai.experience.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senai.experience.entities.ItemPedido;
import com.senai.experience.services.ItemPedidoService;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/itens-pedido")

public class ItemPedidoController {
    @Autowired
    private ItemPedidoService service;

    @PostMapping
    public ResponseEntity<ItemPedido> criar(@RequestBody ItemPedido item) {
        return ResponseEntity.ok(service.save(item));
    }

    @GetMapping
    public List<ItemPedido> listar() {
        return service.listarTodos();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPedido> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemPedido> atualizar(@PathVariable Long id, @RequestBody ItemPedido item) {
        item.setIdItemPedido(id);
        ItemPedido atualizado = service.update(item);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        }
        return ResponseEntity.notFound().build();
    }
}
