package com.senai.experience.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.ProdutoRequest;
import com.senai.experience.DTO.response.ProdutoResponse;
import com.senai.experience.entities.Produto;
import com.senai.experience.mappers.ProdutoMapper;
import com.senai.experience.services.ProdutoService;

@Tag(name = "Produtos", description = "Gerenciamento do catálogo de produtos (modelos de veículos)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/produto")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Operation(summary = "Listar produtos", description = "Retorna todos os produtos cadastrados de forma paginada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    public Page<ProdutoResponse> getAllProdutos(Pageable pageable) {
        return produtoService.findAll(pageable)
                .map(ProdutoMapper::toResponse);
    }

    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo seu identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> getProdutoById(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long id) {
        Produto produto = produtoService.findById(id);
        if (produto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ProdutoMapper.toResponse(produto));
    }

    @Operation(summary = "Cadastrar produto", description = "Cria um novo produto no catálogo. Requer role ADMIN ou VENDEDOR.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado — requer ADMIN ou VENDEDOR", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProdutoResponse> createProduto(@RequestBody @Valid ProdutoRequest dto) {
        Produto salvo = produtoService.save(ProdutoMapper.toEntity(dto));
        return ResponseEntity.status(201).body(ProdutoMapper.toResponse(salvo));
    }

    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente. Requer role ADMIN ou VENDEDOR.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> updateProduto(@PathVariable Long id, @RequestBody @Valid ProdutoRequest dto) {
        Produto p = ProdutoMapper.toEntity(dto);
        p.setIdProduto(id);
        Produto atualizado = produtoService.update(p);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ProdutoMapper.toResponse(atualizado));
    }

    @Operation(summary = "Remover produto", description = "Remove um produto do catálogo. Requer role ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado — requer ADMIN", content = @Content),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long id) {
        produtoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
