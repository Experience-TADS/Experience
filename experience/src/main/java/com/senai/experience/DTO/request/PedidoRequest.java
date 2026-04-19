package com.senai.experience.DTO.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PedidoRequest {
    private int idCliente;
    private int idVendedor;
    private BigDecimal valorTotal;
}
