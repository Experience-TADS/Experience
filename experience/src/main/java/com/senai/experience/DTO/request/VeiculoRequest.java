package com.senai.experience.DTO.request;

import lombok.Data;

@Data
public class VeiculoRequest {
    private int idProduto;
    private int chassi;
    private String statusVeiculo;
}
