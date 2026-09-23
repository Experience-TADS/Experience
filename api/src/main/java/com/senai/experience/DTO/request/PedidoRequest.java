package com.senai.experience.DTO.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoRequest {

    @NotNull(message = "O ID do cliente é obrigatório")
    private Long idCliente;

    @NotNull(message = "O ID do vendedor é obrigatório")
    private Long idVendedor;

    @NotNull(message = "A data do pedido é obrigatória")
    private LocalDateTime dataPedido;

    @NotNull(message = "O valor total é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor total deve ser maior que zero")
    private BigDecimal valorTotal;
}
