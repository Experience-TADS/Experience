package com.senai.experience.mappers;

import com.senai.experience.DTO.request.VeiculoRequest;
import com.senai.experience.DTO.response.VeiculoResponse;
import com.senai.experience.entities.Veiculo;


public class VeiculoMapper {

    public static Veiculo toEntity(VeiculoRequest dto) {
        Veiculo v = new Veiculo();
        v.setIdProduto(dto.getIdProduto());
        v.setChassi(dto.getChassi());
        v.setStatusVeiculo(dto.getStatusVeiculo());
        return v;
    }

    public static VeiculoResponse toResponse(Veiculo v) {
        VeiculoResponse r = new VeiculoResponse();
        r.setId(v.getId());
        r.setIdProduto(v.getIdProduto());
        r.setChassi(v.getChassi());
        r.setStatusVeiculo(v.getStatusVeiculo());
        return r;
    }
}
