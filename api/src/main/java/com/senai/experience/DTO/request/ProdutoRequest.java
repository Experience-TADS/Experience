package com.senai.experience.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "Dados para cadastro ou atualização de um produto (modelo de veículo)")
@Data
public class ProdutoRequest {

    @Schema(description = "Modelo do veículo", example = "Corolla", maxLength = 100)
    @NotBlank(message = "O modelo é obrigatório")
    @Size(max = 100, message = "O modelo deve ter no máximo 100 caracteres")
    private String modelo;

    @Schema(description = "Cor do veículo", example = "Branco Pérola", maxLength = 50)
    @NotBlank(message = "A cor é obrigatória")
    @Size(max = 50, message = "A cor deve ter no máximo 50 caracteres")
    private String cor;

    @Schema(description = "Versão/trim do modelo", example = "XEi", maxLength = 50)
    @NotBlank(message = "A versão é obrigatória")
    @Size(max = 50, message = "A versão deve ter no máximo 50 caracteres")
    private String versao;

    @Schema(description = "Ano de fabricação do modelo", example = "2024", minimum = "1900", maximum = "2100")
    @Min(value = 1900, message = "O ano deve ser maior que 1900")
    @Max(value = 2100, message = "O ano informado é inválido")
    private int ano;

    @Schema(description = "Preço de tabela do produto em reais", example = "149900.00", minimum = "0.01")
    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
    private BigDecimal preco;
}
