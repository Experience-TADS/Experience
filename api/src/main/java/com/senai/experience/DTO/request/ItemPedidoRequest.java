package com.senai.experience.DTO.request;

import lombok.Data;

@Data
public class ItemPedidoRequest {
    private Long idPedido;
    private Long idProduto;
    private Integer quantidade;
}

