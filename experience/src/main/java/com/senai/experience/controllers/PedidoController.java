package com.senai.experience.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.senai.experience.DTO.PedidoRequest;
import com.senai.experience.entities.Pedido;
import com.senai.experience.services.PedidoService;

@RestController //Transforma a classe em um controlador REST, permitindo que ela responda a requisições HTTP
@RequestMapping("/api/pedido")

public class PedidoController {
    @Autowired //Injeta a dependência do serviço de endereço, permitindo que o controlador utilize os métodos definidos no serviço para manipular os dados de endereço
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido != null) {
            return ResponseEntity.ok(pedido);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Pedido> createPedido(@RequestBody PedidoRequest request) {
        return ResponseEntity.ok(pedidoService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable Long id, @RequestBody PedidoRequest request) {
        Pedido atualizado = pedidoService.update(id, request);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
}
}

