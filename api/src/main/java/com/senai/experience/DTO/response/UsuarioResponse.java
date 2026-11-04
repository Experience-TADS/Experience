package com.senai.experience.DTO.response;

import com.senai.experience.entities.role.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Schema(description = "Dados públicos de um usuário retornados pela API")
@Data
public class UsuarioResponse {

    @Schema(description = "Identificador único do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome completo do usuário", example = "Maria Silva")
    private String nome;

    @Schema(description = "E-mail do usuário", example = "maria.silva@email.com")
    private String email;

    @Schema(description = "Data de nascimento", example = "1990-05-20")
    private LocalDate dataNascimento;

    @Schema(description = "Perfil de acesso do usuário", example = "CLIENTE")
    private UserRole role;

    @Schema(description = "Indica se a conta está ativa", example = "true")
    private boolean ativo;
}
