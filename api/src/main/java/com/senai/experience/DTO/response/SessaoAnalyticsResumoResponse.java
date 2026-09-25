package com.senai.experience.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessaoAnalyticsResumoResponse {

    private String secao;
    private Double mediaDuracaoSegundos;
    private Long totalRegistros;
}
