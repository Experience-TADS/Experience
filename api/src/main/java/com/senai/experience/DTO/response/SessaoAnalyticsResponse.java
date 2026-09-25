package com.senai.experience.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessaoAnalyticsResponse {

    private Long id;
    private Long clienteId;
    private String secao;
    private Integer duracaoSegundos;
    private LocalDateTime registradoEm;
}
