package com.senai.experience.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Dados completos de um pedido retornados pela API")
@Data
public class PedidoResponse {

    @Schema(description = "Identificador único do pedido", example = "1")
    private Long id;

    @Schema(description = "Data e hora de criação do pedido", example = "2024-06-15T10:30:00")
    private LocalDateTime dataPedido;

    @Schema(description = "Valor total do pedido em reais", example = "149900.00")
    private BigDecimal valorTotal;

    @Schema(description = "Dados resumidos do cliente vinculado ao pedido")
    private ClienteInfo cliente;

    @Schema(description = "Dados resumidos do vendedor responsável pelo pedido")
    private VendedorInfo vendedor;

    @Schema(description = "Lista de itens (produtos) do pedido")
    private List<ItemInfo> itens;

    @Schema(description = "Dados resumidos do cliente")
    @Data
    public static class ClienteInfo {
        @Schema(description = "ID do cliente", example = "2")
        private Long id;
        @Schema(description = "Nome do cliente", example = "Ana Souza")
        private String nome;
        @Schema(description = "E-mail do cliente", example = "ana.souza@email.com")
        private String email;
    }

    @Schema(description = "Dados resumidos do vendedor")
    @Data
    public static class VendedorInfo {
        @Schema(description = "ID do vendedor", example = "3")
        private Long id;
        @Schema(description = "Nome do vendedor", example = "Carlos Vendedor Toyota")
        private String nome;
    }

    @Schema(description = "Item do pedido com produto e quantidade")
    @Data
    public static class ItemInfo {
        @Schema(description = "ID do item de pedido", example = "1")
        private Long id;
        @Schema(description = "Quantidade de unidades", example = "1")
        private Integer quantidade;
        @Schema(description = "Dados do produto associado ao item")
        private ProdutoInfo produto;
    }

    @Schema(description = "Dados resumidos do produto dentro de um item de pedido")
    @Data
    public static class ProdutoInfo {
        @Schema(description = "ID do produto", example = "1")
        private Long id;
        @Schema(description = "Modelo", example = "Corolla")
        private String modelo;
        @Schema(description = "Cor", example = "Branco Pérola")
        private String cor;
        @Schema(description = "Versão", example = "XEi")
        private String versao;
        @Schema(description = "Ano", example = "2024")
        private Integer ano;
    }
}
