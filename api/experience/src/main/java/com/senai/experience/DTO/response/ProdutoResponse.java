package com.senai.experience.DTO.response;

import lombok.Data;

@Data
public class ProdutoResponse {
    private Long idProduto;
    private String modelo;
    private String cor;
    private String versao;
    private int ano;
}
