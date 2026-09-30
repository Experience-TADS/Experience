package com.senai.experience.DTO.request;

import com.senai.experience.entities.agenda.StatusAgenda;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AtualizarStatusAgendaRequest {

    @NotNull(message = "O status é obrigatório")
    private StatusAgenda status;
}
