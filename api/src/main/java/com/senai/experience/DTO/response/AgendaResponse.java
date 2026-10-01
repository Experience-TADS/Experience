package com.senai.experience.DTO.response;

import java.time.LocalDateTime;

import com.senai.experience.entities.agenda.StatusAgenda;
import com.senai.experience.entities.agenda.TipoAgenda;

import lombok.Data;

@Data
public class AgendaResponse {

    private Long id;
    private LocalDateTime dataHora;
    private TipoAgenda tipo;
    private StatusAgenda status;
    private String observacao;
    private UsuarioInfo colaborador;
    private UsuarioInfo cliente;

    @Data
    public static class UsuarioInfo {
        private Long id;
        private String nome;
        private String email;
    }
}
