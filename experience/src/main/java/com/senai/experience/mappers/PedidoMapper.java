package com.senai.experience.mappers;

import com.senai.experience.DTO.request.PedidoRequest;
import com.senai.experience.DTO.response.PedidoResponse;
import com.senai.experience.entities.Pedido;

public class PedidoMapper {

    public static Pedido toEntity(PedidoRequest dto) {
        Pedido p = new Pedido();
        p.setIdCliente(dto.getIdCliente());
        p.setIdVendedor(dto.getIdVendedor());
        p.setValorTotal(dto.getValorTotal());
        
        return p;
    }

    public static PedidoResponse toResponse(Pedido p) {
        PedidoResponse r = new PedidoResponse();
                r.setId(p.getId());
                r.setIdCliente(p.getIdCliente());
                r.setIdVendedor(p.getIdVendedor());
                r.setValorTotal(p.getValorTotal());
                r.setDataPedido(p.getDataPedido());
        return r;
    }
}
