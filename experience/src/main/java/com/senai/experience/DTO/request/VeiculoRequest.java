package com.senai.experience.DTO.request;

import com.senai.experience.entities.StatusFabricacao;
import lombok.Data;

@Data
public class VeiculoRequest {
    private Long idProduto;
    private int chassi;
    private StatusFabricacao statusVeiculo;
}
