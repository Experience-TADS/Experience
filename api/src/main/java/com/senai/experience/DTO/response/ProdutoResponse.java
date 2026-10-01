package com.senai.experience.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "Dados de um produto (modelo de veículo) retornados pela API")
@Data
public class ProdutoResponse {

    @Schema(description = "Identificador único do produto", example = "1")
    private Long idProduto;

    @Schema(description = "Modelo do veículo", example = "Corolla")
    private String modelo;

    @Schema(description = "Cor do veículo", example = "Branco Pérola")
    private String cor;

    @Schema(description = "Versão/trim do modelo", example = "XEi")
    private String versao;

    @Schema(description = "Ano de fabricação", example = "2024")
    private int ano;

    @Schema(description = "Preço de tabela em reais", example = "149900.00")
    private BigDecimal preco;
}
