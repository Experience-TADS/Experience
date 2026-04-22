package com.senai.experience.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.PedidoRequest;
import com.senai.experience.DTO.response.PedidoResponse;
import com.senai.experience.entities.Pedido;
import com.senai.experience.entities.Usuario;
import com.senai.experience.mappers.PedidoMapper;
import com.senai.experience.repositories.PedidoRepository;
import com.senai.experience.services.PedidoService;
import com.senai.experience.services.UsuarioService;

@RestController
@RequestMapping("/api/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping
    public List<PedidoResponse> getAllPedidos() {
        return pedidoService.findAll()
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> getPedidoById(@PathVariable Long id) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PedidoMapper.toResponse(pedido));
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> createPedido(@RequestBody PedidoRequest dto) {
        Pedido salvo = pedidoService.save(dto);
        return ResponseEntity.status(201).body(PedidoMapper.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> updatePedido(@PathVariable Long id, @RequestBody PedidoRequest dto) {
        Pedido atualizado = pedidoService.update(id, dto);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PedidoMapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Clientes veem apenas os próprios pedidos
    // auth.getName() retorna o email do usuário logado (subject do JWT)
    @GetMapping("/meus-pedidos")
    @PreAuthorize("hasRole('CLIENTE')")
    public List<PedidoResponse> meusPedidos(Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return pedidoRepository.findByIdCliente(usuario)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }
}
