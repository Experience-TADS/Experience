package com.senai.experience.DTO.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

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

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
    private BigDecimal preco;
}
