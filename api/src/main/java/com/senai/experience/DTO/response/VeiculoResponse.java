package com.senai.experience.DTO.response;

import com.senai.experience.entities.StatusFabricacao;
import lombok.Data;

@Data
public class VeiculoResponse {
    private Long id;
    private ProdutoInfo produto;
    private int chassi;
    private StatusFabricacao statusVeiculo;
    private Long idPedido;

    @Data
    public static class ProdutoInfo {
        private Long id;
        private String modelo;
        private String cor;
        private String versao;
        private Integer ano;
    }
}
