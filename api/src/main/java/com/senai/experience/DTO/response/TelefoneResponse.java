package com.senai.experience.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Schema(description = "Dados de um telefone retornados pela API")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelefoneResponse {

    @Schema(description = "Identificador único do telefone", example = "1")
    private Long id;

    @Schema(description = "Número de telefone com DDD, sem formatação", example = "11999998888")
    private String numero;

    @Schema(description = "ID do usuário proprietário do telefone", example = "1")
    private Long idUsuario;
}
