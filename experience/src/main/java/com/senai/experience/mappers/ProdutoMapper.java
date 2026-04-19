package com.senai.experience.mappers;

import com.senai.experience.DTO.request.ProdutoRequest;
import com.senai.experience.DTO.response.ProdutoResponse;
import com.senai.experience.entities.Produto;


public class ProdutoMapper {

    public static Produto toEntity(ProdutoRequest dto) {
        Produto p = new Produto();
        p.setModelo(dto.getModelo());
        p.setCor(dto.getCor());
        p.setVersao(dto.getVersao());
        p.setAno(dto.getAno());
        return p;
    }

    public static ProdutoResponse toResponse(Produto p) {
        ProdutoResponse r = new ProdutoResponse();
        r.setIdProduto(p.getIdProduto());
        r.setModelo(p.getModelo());
        r.setCor(p.getCor());
        r.setVersao(p.getVersao());
        r.setAno(p.getAno());
        return r;
    }
}
