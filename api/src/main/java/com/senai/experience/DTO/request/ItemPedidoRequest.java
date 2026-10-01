package com.senai.experience.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Dados para adicionar um produto a um pedido")
@Data
public class ItemPedidoRequest {

    @Schema(description = "ID do pedido ao qual o item pertence", example = "1")
    @NotNull(message = "O ID do pedido é obrigatório")
    private Long idPedido;

    @Schema(description = "ID do produto a ser adicionado ao pedido", example = "2")
    @NotNull(message = "O ID do produto é obrigatório")
    private Long idProduto;

    @Schema(description = "Quantidade de unidades do produto", example = "1", minimum = "1")
    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser pelo menos 1")
    private Integer quantidade;
}
