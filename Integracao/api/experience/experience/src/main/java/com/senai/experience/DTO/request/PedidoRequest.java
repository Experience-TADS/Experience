package com.senai.experience.DTO.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoRequest {
    private Long idCliente;
    private Long idVendedor;
    private LocalDateTime dataPedido;
    private BigDecimal valorTotal;
}
