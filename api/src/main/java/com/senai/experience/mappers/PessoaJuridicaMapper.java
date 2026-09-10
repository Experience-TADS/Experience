package com.senai.experience.mappers;

import com.senai.experience.DTO.request.PessoaJuridicaRequest;
import com.senai.experience.DTO.response.PessoaJuridicaResponse;
import com.senai.experience.entities.PessoaJuridica;

public class PessoaJuridicaMapper {

    public static PessoaJuridica toEntity(PessoaJuridicaRequest dto) {
        PessoaJuridica p = new PessoaJuridica();
        p.setNome(dto.getNome());
        p.setEmail(dto.getEmail());
        p.setSenhaHash(dto.getSenha());
        p.setDataNascimento(dto.getDataNascimento());
        p.setCnpj(dto.getCnpj());
        p.setRazaoSocial(dto.getRazaoSocial());
        p.setRole(dto.getRole());
        return p;
    }

    public static PessoaJuridicaResponse toResponse(PessoaJuridica p) {
        PessoaJuridicaResponse r = new PessoaJuridicaResponse();
        r.setId(p.getId());
        r.setNome(p.getNome());
        r.setEmail(p.getEmail());
        r.setDataNascimento(p.getDataNascimento());
        r.setCnpj(p.getCnpj());
        r.setRazaoSocial(p.getRazaoSocial());
        r.setRole(p.getRole());
        r.setAtivo(p.isAtivo());
        return r;
    }
}
