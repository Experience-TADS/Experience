package com.senai.experience.mappers;

import com.senai.experience.DTO.request.VeiculoRequest;
import com.senai.experience.DTO.response.VeiculoResponse;
import com.senai.experience.entities.Pedido;
import com.senai.experience.entities.Produto;
import com.senai.experience.entities.Veiculo;

public class VeiculoMapper {

    public static Veiculo toEntity(VeiculoRequest dto) {
        Veiculo v = new Veiculo();
        Produto produto = new Produto();
        produto.setIdProduto(dto.getIdProduto());
        v.setProduto(produto);
        v.setChassi(dto.getChassi());
        v.setStatusVeiculo(dto.getStatusVeiculo());
        if (dto.getIdPedido() != null) {
            Pedido pedido = new Pedido();
            pedido.setId(dto.getIdPedido());
            v.setPedido(pedido);
        }
        return v;
    }

    public static VeiculoResponse toResponse(Veiculo v) {
        VeiculoResponse r = new VeiculoResponse();
        r.setId(v.getId());
        r.setChassi(v.getChassi());
        r.setStatusVeiculo(v.getStatusVeiculo());
        r.setIdPedido(v.getPedido() != null ? v.getPedido().getId() : null);

        if (v.getProduto() != null) {
            VeiculoResponse.ProdutoInfo info = new VeiculoResponse.ProdutoInfo();
            info.setId(v.getProduto().getIdProduto());
            info.setModelo(v.getProduto().getModelo());
            info.setCor(v.getProduto().getCor());
            info.setVersao(v.getProduto().getVersao());
            info.setAno(v.getProduto().getAno());
            r.setProduto(info);
        }

        return r;
    }
}
