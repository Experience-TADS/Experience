package com.senai.experience.DTO.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemPedidoRequest {

    @NotNull(message = "O ID do pedido é obrigatório")
    private Long idPedido;

    @NotNull(message = "O ID do produto é obrigatório")
    private Long idProduto;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser pelo menos 1")
    private Integer quantidade;
}
