package com.senai.experience.DTO.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoResponse {
    private Long id;
    private LocalDateTime dataPedido;
    private BigDecimal valorTotal;
    private ClienteInfo cliente;
    private VendedorInfo vendedor;
    private List<ItemInfo> itens;

    @Data
    public static class ClienteInfo {
        private Long id;
        private String nome;
        private String email;
    }
    
    @Data
    public static class VendedorInfo {
        private Long id;
        private String nome;
    }
    
    @Data
    public static class ItemInfo {
        private Long id;
        private Integer quantidade;
        private ProdutoInfo produto;
    }
    
    @Data
    public static class ProdutoInfo {
        private Long id;
        private String modelo;
        private String cor;
        private String versao;
        private Integer ano;
    }
}
