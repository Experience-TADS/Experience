package com.senai.experience.controllers;

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
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.ItemPedidoRequest;
import com.senai.experience.DTO.response.ItemPedidoResponse;
import com.senai.experience.entities.ItemPedido;
import com.senai.experience.mappers.ItemPedidoMapper;
import com.senai.experience.services.ItemPedidoService;
import lombok.RequiredArgsConstructor;

@Tag(name = "Itens de Pedido", description = "Gerenciamento dos itens (produtos) vinculados a pedidos")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/itens-pedido")
public class ItemPedidoController {

    private final ItemPedidoService service;

    @Operation(summary = "Listar itens de pedido", description = "Retorna todos os itens de pedido cadastrados de forma paginada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    public Page<ItemPedidoResponse> getAllItemPedidos(Pageable pageable) {
        return service.listarTodos(pageable)
                .map(ItemPedidoMapper::toResponse);
    }

    @Operation(summary = "Buscar item por ID", description = "Retorna um item de pedido específico pelo seu identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item encontrado"),
        @ApiResponse(responseCode = "404", description = "Item não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ItemPedidoResponse> buscarPorId(
            @Parameter(description = "ID do item de pedido", example = "1") @PathVariable Long id) {
        ItemPedido item = service.findById(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ItemPedidoMapper.toResponse(item));
    }

    @Operation(summary = "Adicionar item ao pedido", description = "Vincula um produto a um pedido com a quantidade especificada.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Item criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos — quantidade mínima é 1", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ItemPedidoResponse> createItemPedido(@RequestBody ItemPedidoRequest dto) {
        ItemPedido salvo = service.save(ItemPedidoMapper.toEntity(dto));
        return ResponseEntity.status(201).body(ItemPedidoMapper.toResponse(salvo));
    }

    @Operation(summary = "Atualizar item de pedido", description = "Atualiza a quantidade ou o produto de um item existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Item não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ItemPedidoResponse> updateItemPedido(
            @Parameter(description = "ID do item de pedido", example = "1") @PathVariable Long id,
            @RequestBody ItemPedidoRequest dto) {
        ItemPedido item = ItemPedidoMapper.toEntity(dto);
        item.setIdItemPedido(id);
        ItemPedido atualizado = service.update(item);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ItemPedidoMapper.toResponse(atualizado));
    }

    @Operation(summary = "Remover item de pedido", description = "Remove um item de pedido pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Item removido com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Item não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do item de pedido", example = "1") @PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
