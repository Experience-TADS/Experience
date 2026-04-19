package com.senai.experience.mappers;

import com.senai.experience.DTO.request.ItemPedidoRequest;
import com.senai.experience.DTO.response.ItemPedidoResponse;
import com.senai.experience.entities.ItemPedido;
import com.senai.experience.entities.Produto;

public class ItemPedidoMapper {

    public static ItemPedido toEntity(ItemPedidoRequest dto) {
        ItemPedido i = new ItemPedido();
        // ItemPedido usa @ManyToOne Produto, então montamos a referência pelo id
        Produto produto = new Produto();
        produto.setIdProduto(dto.getIdProduto());
        i.setProduto(produto);
        i.setQuantidade(dto.getQuantidade());
        return i;
    }

    public static ItemPedidoResponse toResponse(ItemPedido i) {
        ItemPedidoResponse r = new ItemPedidoResponse();
        r.setIdItemPedido(i.getIdItemPedido());
        if (i.getProduto() != null) {
            r.setIdProduto(i.getProduto().getIdProduto());
            r.setModeloProduto(i.getProduto().getModelo());
        }
        r.setQuantidade(i.getQuantidade());
        return r;
    }
}
