package com.senai.experience.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.senai.experience.entities.Produto;
import com.senai.experience.entities.Veiculo;
import com.senai.experience.repositories.ProdutoRepository;
import com.senai.experience.repositories.VeiculoRepository;

@Service
public class VeiculoService {
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Veiculo> findAll() { 
        return veiculoRepository.findAll();
    }
    public Veiculo findById(Long id) {
        return veiculoRepository.findById(id).orElse(null);
    }
    public Veiculo save(Veiculo veiculo) {
        // Carrega o Produto completo do banco antes de salvar
        if (veiculo.getProduto() != null && veiculo.getProduto().getIdProduto() != null) {
            Produto produto = produtoRepository.findById(veiculo.getProduto().getIdProduto())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + veiculo.getProduto().getIdProduto()));
            veiculo.setProduto(produto);
        }
        return veiculoRepository.save(veiculo);
    }
    public Veiculo update(@PathVariable Long id, Veiculo veiculo) {
        Veiculo existing = findById(id);
        if (existing != null) {
            existing.setStatusVeiculo(veiculo.getStatusVeiculo());
            return veiculoRepository.save(existing);
        }
        return null;
    }
    public void delete(Long id) {
        veiculoRepository.deleteById(id);
    }

    public Veiculo findByChassi(int chassi){
        return veiculoRepository.findByChassi(chassi);
    }
}