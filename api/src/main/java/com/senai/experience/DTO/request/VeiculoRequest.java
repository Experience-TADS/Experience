package com.senai.experience.DTO.request;

import com.senai.experience.entities.StatusFabricacao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VeiculoRequest {

    @NotNull(message = "O ID do produto é obrigatório")
    private Long idProduto;

    @Min(value = 1, message = "O chassi deve ser um número positivo")
    private int chassi;

    @NotNull(message = "O status do veículo é obrigatório")
    private StatusFabricacao statusVeiculo;

    private Long idPedido; // opcional
}
