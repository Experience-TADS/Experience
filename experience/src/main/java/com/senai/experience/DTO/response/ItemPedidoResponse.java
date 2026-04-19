package com.senai.experience.DTO.response;

import lombok.Data;

@Data
public class ItemPedidoResponse {
    private Long idItemPedido;
    private Long idProduto;
    private String modeloProduto;   
    private Integer quantidade;
}
