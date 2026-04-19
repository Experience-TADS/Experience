package com.senai.experience.mappers;

import com.senai.experience.DTO.request.VeiculoRequest;
import com.senai.experience.DTO.response.VeiculoResponse;
import com.senai.experience.entities.Produto;
import com.senai.experience.entities.Veiculo;

public class VeiculoMapper {

    public static Veiculo toEntity(VeiculoRequest dto) {
        Veiculo v = new Veiculo();
        // Veiculo usa @ManyToOne Produto — monta a referência pelo id
        Produto produto = new Produto();
        produto.setIdProduto(dto.getIdProduto());
        v.setProduto(produto);
        v.setChassi(dto.getChassi());
        v.setStatusVeiculo(dto.getStatusVeiculo());
        return v;
    }

    public static VeiculoResponse toResponse(Veiculo v) {
        VeiculoResponse r = new VeiculoResponse();
        r.setId(v.getId());
        r.setIdProduto(v.getProduto() != null ? v.getProduto().getIdProduto() : null);
        r.setChassi(v.getChassi());
        r.setStatusVeiculo(v.getStatusVeiculo());
        return r;
    }
}
