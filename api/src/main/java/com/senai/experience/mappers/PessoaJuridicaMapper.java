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

    /**
     * Aplica os campos do DTO sobre uma entidade já gerenciada (fluxo de update).
     * Preserva o id e o estado de persistência da entidade existente, evitando
     * conflitos de constraint no commit. A senha vem como texto puro do request;
     * o service reencoda conforme necessário.
     */
    public static void updateEntity(PessoaJuridica destino, PessoaJuridicaRequest dto) {
        destino.setNome(dto.getNome());
        destino.setEmail(dto.getEmail());
        destino.setSenhaHash(dto.getSenha());
        destino.setDataNascimento(dto.getDataNascimento());
        destino.setCnpj(dto.getCnpj());
        destino.setRazaoSocial(dto.getRazaoSocial());
        destino.setRole(dto.getRole());
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
