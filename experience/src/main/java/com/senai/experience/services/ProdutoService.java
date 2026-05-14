package com.senai.experience.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.senai.experience.entities.Produto;
import com.senai.experience.repositories.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }
    
    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }
    
    public void deleteById(Long id) {
        produtoRepository.deleteById(id);
    }

    public Produto findById(Long id) {
        return produtoRepository.findById(id).orElse(null);
    }

    public Produto update(Produto produto) {
        if (produto.getIdProduto() != null && produtoRepository.existsById(produto.getIdProduto())) {
            return produtoRepository.save(produto);
        }
        return null;
    }

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

}
