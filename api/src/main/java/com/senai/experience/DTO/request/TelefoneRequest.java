package com.senai.experience.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TelefoneRequest {

    @NotBlank(message = "O número de telefone é obrigatório")
    @Pattern(
        regexp = "\\d{10,11}",
        message = "O telefone deve conter 10 ou 11 dígitos numéricos (DDD + número)"
    )
    private String numero;

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long idUsuario;
}
