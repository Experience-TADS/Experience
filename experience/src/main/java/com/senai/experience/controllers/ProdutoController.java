package com.senai.experience.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.ProdutoRequest;
import com.senai.experience.DTO.response.ProdutoResponse;
import com.senai.experience.entities.Produto;
import com.senai.experience.mappers.ProdutoMapper;
import com.senai.experience.services.ProdutoService;

@RestController
@RequestMapping("/api/produto")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<ProdutoResponse> getAllProdutos() {
        return produtoService.findAll()
                .stream()
                .map(ProdutoMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> getProdutoById(@PathVariable Long id) {
        Produto produto = produtoService.findById(id);
        if (produto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ProdutoMapper.toResponse(produto));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> createProduto(@RequestBody ProdutoRequest dto) {
        Produto salvo = produtoService.save(ProdutoMapper.toEntity(dto));
        return ResponseEntity.status(201).body(ProdutoMapper.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> updateProduto(@PathVariable Long id, @RequestBody ProdutoRequest dto) {
        Produto p = ProdutoMapper.toEntity(dto);
        p.setIdProduto(id);
        Produto atualizado = produtoService.update(p);
        if (atualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ProdutoMapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long id) {
        produtoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
