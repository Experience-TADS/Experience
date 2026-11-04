package com.senai.experience.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Resumo agregado de analytics por seção do aplicativo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessaoAnalyticsResumoResponse {

    @Schema(description = "Nome da seção do aplicativo", example = "catalogo-produtos")
    private String secao;

    @Schema(description = "Média de duração das sessões nesta seção em segundos", example = "95.5")
    private Double mediaDuracaoSegundos;

    @Schema(description = "Total de sessões registradas para esta seção", example = "42")
    private Long totalRegistros;
}
