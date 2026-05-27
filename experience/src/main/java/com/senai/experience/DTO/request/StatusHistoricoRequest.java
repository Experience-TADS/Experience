package com.senai.experience.DTO.request;

import java.time.LocalDateTime;

import com.senai.experience.entities.StatusFabricacao;


import lombok.Data;

@Data
public class StatusHistoricoRequest {

   private Long id;
    private Long veiculoId;
    private StatusFabricacao status;
    private LocalDateTime dataAlteracao;
}
