package com.senai.experience.DTO.request;

import lombok.Data;

@Data
public class ProdutoRequest {
    private String modelo;
    private String cor;
    private String versao;
    private int ano;
}
