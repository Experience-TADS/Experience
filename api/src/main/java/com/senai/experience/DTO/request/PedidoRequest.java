package com.senai.experience.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados para criação ou atualização de um pedido de compra")
@Data
public class PedidoRequest {

    @Schema(description = "ID do cliente vinculado ao pedido", example = "2")
    @NotNull(message = "O ID do cliente é obrigatório")
    private Long idCliente;

    @Schema(description = "ID do vendedor responsável pelo pedido", example = "3")
    @NotNull(message = "O ID do vendedor é obrigatório")
    private Long idVendedor;

    @Schema(description = "Data e hora de criação do pedido", example = "2024-06-15T10:30:00")
    @NotNull(message = "A data do pedido é obrigatória")
    private LocalDateTime dataPedido;

    @Schema(description = "Valor total do pedido em reais", example = "149900.00", minimum = "0.01")
    @NotNull(message = "O valor total é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor total deve ser maior que zero")
    private BigDecimal valorTotal;
}
