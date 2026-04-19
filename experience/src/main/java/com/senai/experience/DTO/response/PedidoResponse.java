package com.senai.experience.DTO.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoResponse {
    private Long id;
    private int idCliente;
    private int idVendedor;
    private LocalDateTime dataPedido;
    private BigDecimal valorTotal;
}
