package com.senai.experience.DTO.response;

import com.senai.experience.entities.StatusFabricacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private Long veiculoId;
    private String modelo;
    private String cor;
    private String versao;
    private StatusFabricacao statusAtual;
    private LocalDateTime ultimaAtualizacao;
}
