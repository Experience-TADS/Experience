package com.senai.experience.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Credenciais para autenticação na API")
@Data
public class LoginRequest {

    @Schema(description = "E-mail cadastrado do usuário", example = "admin@experience.com")
    private String email;

    @Schema(description = "Senha do usuário", example = "admin123")
    private String senha;
}
