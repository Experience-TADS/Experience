package com.senai.experience.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Dados de um item de pedido retornados pela API")
@Data
public class ItemPedidoResponse {

    @Schema(description = "Identificador único do item de pedido", example = "1")
    private Long idItemPedido;

    @Schema(description = "Quantidade de unidades do produto no pedido", example = "1")
    private Integer quantidade;

    @Schema(description = "Dados resumidos do produto vinculado ao item")
    private ProdutoInfo produto;

    @Schema(description = "Dados resumidos do produto dentro de um item de pedido")
    @Data
    public static class ProdutoInfo {
        @Schema(description = "ID do produto", example = "1")
        private Long id;
        @Schema(description = "Modelo", example = "Hilux")
        private String modelo;
        @Schema(description = "Cor", example = "Prata Metálico")
        private String cor;
        @Schema(description = "Versão", example = "SRX")
        private String versao;
        @Schema(description = "Ano", example = "2024")
        private Integer ano;
    }
}
