package com.senai.experience.mappers;

import com.senai.experience.DTO.request.EnderecoRequest;
import com.senai.experience.DTO.response.EnderecoResponse;
import com.senai.experience.entities.Endereco;
import com.senai.experience.entities.Usuario;

public class EnderecoMapper {

    public static Endereco toEntity(EnderecoRequest dto) {
        Endereco e = new Endereco();
        e.setCep(dto.getCep());
        e.setLogradouro(dto.getLogradouro());
        e.setNumero(dto.getNumero());
        e.setBairro(dto.getBairro());
        e.setCidade(dto.getCidade());
        e.setEstado(dto.getEstado());
        
        Usuario usuario = new Usuario();
        usuario.setId(dto.getIdUsuario());
        e.setUsuario(usuario);
        
        return e;
    }

    public static EnderecoResponse toResponse(Endereco e) {
        EnderecoResponse r = new EnderecoResponse();
        r.setId(e.getId());
        r.setCep(e.getCep());
        r.setLogradouro(e.getLogradouro());
        r.setNumero(e.getNumero());
        r.setBairro(e.getBairro());
        r.setCidade(e.getCidade());
        r.setEstado(e.getEstado());
        r.setIdUsuario(e.getUsuario() != null ? e.getUsuario().getId() : null);
        return r;
    }
}
