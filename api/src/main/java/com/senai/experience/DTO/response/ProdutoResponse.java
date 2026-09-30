package com.senai.experience.DTO.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoResponse {
    private Long idProduto;
    private String modelo;
    private String cor;
    private String versao;
    private int ano;
    private BigDecimal preco;
}
