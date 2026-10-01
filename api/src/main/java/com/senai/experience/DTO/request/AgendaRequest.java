package com.senai.experience.DTO.request;

import java.time.LocalDateTime;

import com.senai.experience.entities.agenda.TipoAgenda;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AgendaRequest {

    @NotNull(message = "O ID do cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "A data e hora são obrigatórias")
    @Future(message = "A data deve ser futura")
    private LocalDateTime dataHora;

    @NotNull(message = "O tipo de agendamento é obrigatório")
    private TipoAgenda tipo;

    private String observacao;
}
