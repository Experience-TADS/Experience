package com.senai.experience.mappers;

import com.senai.experience.DTO.request.PessoaFisicaRequest;
import com.senai.experience.DTO.response.PessoaFisicaResponse;
import com.senai.experience.entities.PessoaFisica;

public class PessoaFisicaMapper {

    public static PessoaFisica toEntity(PessoaFisicaRequest dto) {
        PessoaFisica p = new PessoaFisica();
        p.setNome(dto.getNome());
        p.setEmail(dto.getEmail());
        p.setSenhaHash(dto.getSenha());
        p.setDataNascimento(dto.getDataNascimento());
        p.setCpf(dto.getCpf());
        p.setRole(dto.getRole());
        return p;
    }

    public static PessoaFisicaResponse toResponse(PessoaFisica p) {
        PessoaFisicaResponse r = new PessoaFisicaResponse();
        r.setId(p.getId());
        r.setNome(p.getNome());
        r.setEmail(p.getEmail());
        r.setDataNascimento(p.getDataNascimento());
        r.setCpf(p.getCpf());
        r.setRole(p.getRole());
        r.setAtivo(p.isAtivo());
        return r;
    }
}
