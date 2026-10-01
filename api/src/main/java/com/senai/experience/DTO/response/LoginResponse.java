package com.senai.experience.DTO.response;

import com.senai.experience.entities.role.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;

@Schema(description = "Resposta do endpoint de login contendo o token JWT e dados básicos do usuário")
@Data
@AllArgsConstructor
public class LoginResponse {

    @Schema(description = "Token JWT para autenticação nas requisições subsequentes", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "E-mail do usuário autenticado", example = "admin@experience.com")
    private String email;

    @Schema(description = "Perfil de acesso do usuário autenticado", example = "ADMIN")
    private UserRole role;
}
