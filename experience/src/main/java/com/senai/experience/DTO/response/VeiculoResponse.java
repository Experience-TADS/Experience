package com.senai.experience.DTO.response;

import lombok.Data;

@Data
public class VeiculoResponse {
    private Long id;
    private int idProduto;
    private int chassi;
    private String statusVeiculo;
}
