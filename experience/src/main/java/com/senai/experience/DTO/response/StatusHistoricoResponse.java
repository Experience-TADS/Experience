package com.senai.experience.DTO.response;

import java.time.LocalDateTime;

import com.senai.experience.entities.StatusFabricacao;


import lombok.Data;


@Data
public class StatusHistoricoResponse {
    private Long id;
    private Long veiculoId;
    private StatusFabricacao status;
    private LocalDateTime dataAlteracao;

}