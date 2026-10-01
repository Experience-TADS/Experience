package com.senai.experience.controllers;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.PedidoRequest;
import com.senai.experience.DTO.response.PedidoResponse;
import com.senai.experience.entities.Pedido;
import com.senai.experience.mappers.PedidoMapper;
import com.senai.experience.services.PedidoService;
import lombok.RequiredArgsConstructor;

@Tag(name = "Pedidos", description = "Gerenciamento de pedidos de compra de veículos")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/pedido")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Listar pedidos", description = "Retorna todos os pedidos cadastrados de forma paginada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    public Page<PedidoResponse> getAllPedidos(Pageable pageable) {
        return pedidoService.findAll(pageable)
                .map(PedidoMapper::toResponse);
    }

    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido específico com seus itens, cliente e vendedor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> getPedidoById(
            @Parameter(description = "ID do pedido", example = "1") @PathVariable Long id) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PedidoMapper.toResponse(pedido));
    }

    @Operation(summary = "Criar pedido", description = "Registra um novo pedido de compra vinculando cliente, vendedor e valor total.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PedidoResponse> createPedido(@RequestBody PedidoRequest dto) {
        Pedido salvo = pedidoService.save(dto);
        return ResponseEntity.status(201).body(PedidoMapper.toResponse(salvo));
    }

    @Operation(summary = "Atualizar pedido", description = "Atualiza os dados de um pedido existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> updatePedido(
            @Parameter(description = "ID do pedido", example = "1") @PathVariable Long id,
            @RequestBody PedidoRequest dto) {
        Pedido atualizado = pedidoService.update(id, dto);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PedidoMapper.toResponse(atualizado));
    }

    @Operation(summary = "Remover pedido", description = "Remove um pedido pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pedido removido com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(
            @Parameter(description = "ID do pedido", example = "1") @PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Meus pedidos",
        description = "Retorna os pedidos do usuário autenticado. Disponível para CLIENTE, VENDEDOR e ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedidos retornados com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/meus-pedidos")
    @PreAuthorize("hasAnyRole('CLIENTE', 'VENDEDOR', 'ADMIN')")
    public List<PedidoResponse> meusPedidos(Authentication auth) {
        return pedidoService.findMeusPedidos(auth.getName())
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }
}
