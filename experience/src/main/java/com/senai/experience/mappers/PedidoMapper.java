package com.senai.experience.mappers;

import com.senai.experience.DTO.response.PedidoResponse;
import com.senai.experience.entities.Pedido;
import com.senai.experience.entities.ItemPedido;
import java.util.stream.Collectors;

public class PedidoMapper {

    public static PedidoResponse toResponse(Pedido p) {
        PedidoResponse r = new PedidoResponse();
        r.setId(p.getId());
        r.setDataPedido(p.getDataPedido());
        r.setValorTotal(p.getValorTotal());
        
        if (p.getIdCliente() != null) {
            PedidoResponse.ClienteInfo cliente = new PedidoResponse.ClienteInfo();
            cliente.setId(p.getIdCliente().getId());
            cliente.setNome(p.getIdCliente().getNome());
            cliente.setEmail(p.getIdCliente().getEmail());
            r.setCliente(cliente);
        }
        
        if (p.getIdVendedor() != null) {
            PedidoResponse.VendedorInfo vendedor = new PedidoResponse.VendedorInfo();
            vendedor.setId(p.getIdVendedor().getId());
            vendedor.setNome(p.getIdVendedor().getNome());
            r.setVendedor(vendedor);
        }
        
        if (p.getItens() != null && !p.getItens().isEmpty()) {
            r.setItens(p.getItens().stream()
                .map(item -> {
                    PedidoResponse.ItemInfo itemInfo = new PedidoResponse.ItemInfo();
                    itemInfo.setId(item.getIdItemPedido());
                    itemInfo.setQuantidade(item.getQuantidade());
                    
                    if (item.getProduto() != null) {
                        PedidoResponse.ProdutoInfo produtoInfo = new PedidoResponse.ProdutoInfo();
                        produtoInfo.setId(item.getProduto().getIdProduto());
                        produtoInfo.setModelo(item.getProduto().getModelo());
                        produtoInfo.setCor(item.getProduto().getCor());
                        produtoInfo.setVersao(item.getProduto().getVersao());
                        produtoInfo.setAno(item.getProduto().getAno());
                        itemInfo.setProduto(produtoInfo);
                    }
                    
                    return itemInfo;
                })
                .collect(Collectors.toList())
            );
        }
        
        return r;
    }
}
