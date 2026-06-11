package com.senai.experience.DTO.response;

import lombok.Data;

@Data
public class ItemPedidoResponse {
    private Long idItemPedido;
    private Integer quantidade;
    private ProdutoInfo produto;
    
    @Data
    public static class ProdutoInfo {
        private Long id;
        private String modelo;
        private String cor;
        private String versao;
        private Integer ano;
    }
}
