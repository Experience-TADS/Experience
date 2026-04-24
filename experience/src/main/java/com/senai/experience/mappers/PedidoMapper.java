package com.senai.experience.mappers;

import com.senai.experience.DTO.response.PedidoResponse;
import com.senai.experience.entities.Pedido;

public class PedidoMapper {

    public static PedidoResponse toResponse(Pedido p) {
        PedidoResponse r = new PedidoResponse();
        r.setId(p.getId());
        r.setIdCliente(p.getIdCliente() != null ? p.getIdCliente().getId() : null);
        r.setIdVendedor(p.getIdVendedor() != null ? p.getIdVendedor().getId() : null);
        r.setValorTotal(p.getValorTotal());
        r.setDataPedido(p.getDataPedido());
        return r;
    }
}
