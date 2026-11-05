package com.senai.experience.DTO.request;

import com.senai.experience.entities.StatusFabricacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Dados para cadastro ou atualização de um veículo")
@Data
public class VeiculoRequest {

    @Schema(description = "ID do produto (modelo) vinculado ao veículo", example = "1")
    @NotNull(message = "O ID do produto é obrigatório")
    private Long idProduto;

    @Schema(description = "Número único do chassi do veículo", example = "10001", minimum = "1")
    @Min(value = 1, message = "O chassi deve ser um número positivo")
    private int chassi;

    @Schema(description = "Status atual de fabricação do veículo", example = "AGUARDANDO",
        allowableValues = {"AGUARDANDO", "MONTAGEM_ESTRUTURAL", "PINTURA", "INSTALACAO_MOTOR",
                           "ACABAMENTO_INTERNO", "INSPECAO_FINAL", "LIBERACAO_TRANSPORTE", "NA_CONCESSIONARIA"})
    @NotNull(message = "O status do veículo é obrigatório")
    private StatusFabricacao statusVeiculo;

    @Schema(description = "ID do pedido vinculado ao veículo (opcional)", example = "5")
    private Long idPedido;
}
