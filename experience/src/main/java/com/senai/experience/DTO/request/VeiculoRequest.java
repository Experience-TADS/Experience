package com.senai.experience.DTO.request;

import lombok.Data;

@Data
public class VeiculoRequest {
    private Long idProduto;
    private int chassi;
    private String statusVeiculo;
}
