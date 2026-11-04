package com.senai.experience.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "Dados para cadastro ou atualização de um telefone")
@Data
public class TelefoneRequest {

    @Schema(
        description = "Número de telefone com DDD, sem formatação. 10 dígitos para fixo, 11 dígitos para celular com nono dígito.",
        example = "11999998888"
    )
    @NotBlank(message = "O número de telefone é obrigatório")
    @Pattern(
        regexp = "\\d{10,11}",
        message = "O telefone deve conter 10 ou 11 dígitos numéricos (DDD + número)"
    )
    private String numero;

    @Schema(description = "ID do usuário proprietário do telefone", example = "1")
    @NotNull(message = "O ID do usuário é obrigatório")
    private Long idUsuario;
}
