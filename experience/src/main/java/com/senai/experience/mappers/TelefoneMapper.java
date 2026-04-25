package com.senai.experience.mappers;

import com.senai.experience.DTO.request.TelefoneRequest;
import com.senai.experience.DTO.response.TelefoneResponse;
import com.senai.experience.entities.Telefone;
import com.senai.experience.entities.Usuario;

public class TelefoneMapper {
    
    public static Telefone toEntity(TelefoneRequest dto) {
        Telefone t = new Telefone();
        t.setNumero(dto.getNumero());
        
        // Cria referência ao usuário apenas com o ID
        Usuario usuario = new Usuario();
        usuario.setId(dto.getIdUsuario());
        t.setUsuario(usuario);
        
        return t;
    }

    public static TelefoneResponse toResponse(Telefone t) {
        TelefoneResponse r = new TelefoneResponse();
        r.setId(t.getId());
        r.setNumero(t.getNumero());
        r.setIdUsuario(t.getUsuario() != null ? t.getUsuario().getId() : null);
        return r;
    }
}
