package com.senai.experience.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "Dados de uma sessão de analytics registrada")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessaoAnalyticsResponse {

    @Schema(description = "Identificador único da sessão", example = "1")
    private Long id;

    @Schema(description = "ID do cliente que gerou a sessão", example = "3")
    private Long clienteId;

    @Schema(description = "Nome da seção visitada no aplicativo", example = "catalogo-produtos")
    private String secao;

    @Schema(description = "Tempo de permanência na seção em segundos", example = "120")
    private Integer duracaoSegundos;

    @Schema(description = "Data e hora em que a sessão foi registrada", example = "2024-06-15T14:22:00")
    private LocalDateTime registradoEm;
}
