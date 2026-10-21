package com.senai.experience.DTO.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Credenciais para autenticação na API")
@Data
public class LoginRequest {

    @Schema(description = "E-mail cadastrado do usuário", example = "admin@experience.com")
    private String email;

    @Schema(description = "Senha do usuário", example = "admin123")
    // S-011: senha fora da sanitização XSS global — precisa bater exatamente com o hash.
    @JsonDeserialize(using = StringDeserializer.class)
    private String senha;
}
