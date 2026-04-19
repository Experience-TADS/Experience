package com.senai.experience.mappers;

import com.senai.experience.DTO.request.UsuarioRequest;
import com.senai.experience.DTO.response.UsuarioResponse;
import com.senai.experience.entities.Usuario;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRequest dto) {
        Usuario u = new Usuario();
        u.setNome(dto.getNome());
        u.setEmail(dto.getEmail());
        u.setSenhaHash(dto.getSenha());
        u.setDataNascimento(dto.getDataNascimento());
        u.setRole(dto.getRole());
        return u;
    }

    public static UsuarioResponse toResponse(Usuario u) {
        UsuarioResponse r = new UsuarioResponse();
        r.setId(u.getId());
        r.setNome(u.getNome());
        r.setEmail(u.getEmail());
        r.setDataNascimento(u.getDataNascimento());
        r.setRole(u.getRole());
        return r;
    }
}
