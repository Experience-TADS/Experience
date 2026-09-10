package com.senai.experience.DTO.request;

import lombok.Data;

@Data
public class EnderecoRequest {
    private String cep;
    private String logradouro;
    private int numero;
    private String bairro;
    private String cidade;
    private String estado;
    private Long idUsuario;

}
