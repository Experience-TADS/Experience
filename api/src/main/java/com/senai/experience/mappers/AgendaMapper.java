package com.senai.experience.mappers;

import com.senai.experience.DTO.response.AgendaResponse;
import com.senai.experience.entities.Agenda;
import com.senai.experience.entities.Usuario;

public class AgendaMapper {

    private AgendaMapper() {}

    public static AgendaResponse toResponse(Agenda agenda) {
        AgendaResponse response = new AgendaResponse();
        response.setId(agenda.getId());
        response.setDataHora(agenda.getDataHora());
        response.setTipo(agenda.getTipo());
        response.setStatus(agenda.getStatus());
        response.setObservacao(agenda.getObservacao());
        response.setColaborador(toUsuarioInfo(agenda.getColaborador()));
        response.setCliente(toUsuarioInfo(agenda.getCliente()));
        return response;
    }

    private static AgendaResponse.UsuarioInfo toUsuarioInfo(Usuario usuario) {
        if (usuario == null) return null;
        AgendaResponse.UsuarioInfo info = new AgendaResponse.UsuarioInfo();
        info.setId(usuario.getId());
        info.setNome(usuario.getNome());
        info.setEmail(usuario.getEmail());
        return info;
    }
}
