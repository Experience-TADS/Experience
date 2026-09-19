package com.senai.experience.DTO.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProdutoRequest {

    @NotBlank(message = "O modelo é obrigatório")
    @Size(max = 100, message = "O modelo deve ter no máximo 100 caracteres")
    private String modelo;

    @NotBlank(message = "A cor é obrigatória")
    @Size(max = 50, message = "A cor deve ter no máximo 50 caracteres")
    private String cor;

    @NotBlank(message = "A versão é obrigatória")
    @Size(max = 50, message = "A versão deve ter no máximo 50 caracteres")
    private String versao;

    @Min(value = 1900, message = "O ano deve ser maior que 1900")
    @Max(value = 2100, message = "O ano informado é inválido")
    private int ano;
}
