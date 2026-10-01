package com.senai.experience.DTO.response;

import com.senai.experience.entities.StatusFabricacao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Dados de um veículo retornados pela API")
@Data
public class VeiculoResponse {

    @Schema(description = "Identificador único do veículo", example = "1")
    private Long id;

    @Schema(description = "Dados resumidos do produto (modelo) vinculado ao veículo")
    private ProdutoInfo produto;

    @Schema(description = "Número único do chassi do veículo", example = "10001")
    private int chassi;

    @Schema(description = "Status atual de fabricação do veículo", example = "PINTURA",
        allowableValues = {"AGUARDANDO", "MONTAGEM_ESTRUTURAL", "PINTURA", "INSTALACAO_MOTOR",
                           "ACABAMENTO_INTERNO", "INSPECAO_FINAL", "LIBERACAO_TRANSPORTE", "NA_CONCESSIONARIA"})
    private StatusFabricacao statusVeiculo;

    @Schema(description = "ID do pedido vinculado ao veículo", example = "5")
    private Long idPedido;

    @Schema(description = "Dados resumidos do produto dentro da resposta de veículo")
    @Data
    public static class ProdutoInfo {
        @Schema(description = "ID do produto", example = "1")
        private Long id;
        @Schema(description = "Modelo", example = "Corolla")
        private String modelo;
        @Schema(description = "Cor", example = "Branco Pérola")
        private String cor;
        @Schema(description = "Versão", example = "XEi")
        private String versao;
        @Schema(description = "Ano", example = "2024")
        private Integer ano;
    }
}
