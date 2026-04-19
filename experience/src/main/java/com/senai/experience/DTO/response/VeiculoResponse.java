package com.senai.experience.DTO.response;

import com.senai.experience.entities.StatusFabricacao;
import lombok.Data;

@Data
public class VeiculoResponse {
    private Long id;
    private Long idProduto;
    private int chassi;
    private StatusFabricacao statusVeiculo;
}
