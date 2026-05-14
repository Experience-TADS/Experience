package com.senai.experience.mappers;

import com.senai.experience.DTO.request.ItemPedidoRequest;
import com.senai.experience.DTO.response.ItemPedidoResponse;
import com.senai.experience.entities.ItemPedido;
import com.senai.experience.entities.Pedido;
import com.senai.experience.entities.Produto;

public class ItemPedidoMapper {

    public static ItemPedido toEntity(ItemPedidoRequest dto) {
        ItemPedido i = new ItemPedido();
        
        // Cria referência ao Pedido pelo id
        if (dto.getIdPedido() != null) {
            Pedido pedido = new Pedido();
            pedido.setId(dto.getIdPedido());
            i.setPedido(pedido);
        }
        
        if (dto.getIdProduto() != null) {
            Produto produto = new Produto();
            produto.setIdProduto(dto.getIdProduto());
            i.setProduto(produto);
        }
        
        i.setQuantidade(dto.getQuantidade());
        return i;
    }

    public static ItemPedidoResponse toResponse(ItemPedido i) {
        ItemPedidoResponse r = new ItemPedidoResponse();
        r.setIdItemPedido(i.getIdItemPedido());
        r.setQuantidade(i.getQuantidade());
        
        if (i.getProduto() != null) {
            ItemPedidoResponse.ProdutoInfo produtoInfo = new ItemPedidoResponse.ProdutoInfo();
            produtoInfo.setId(i.getProduto().getIdProduto());
            produtoInfo.setModelo(i.getProduto().getModelo());
            produtoInfo.setCor(i.getProduto().getCor());
            produtoInfo.setVersao(i.getProduto().getVersao());
            produtoInfo.setAno(i.getProduto().getAno());
            r.setProduto(produtoInfo);
        }
        
        return r;
    }
}
