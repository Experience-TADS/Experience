package com.senai.experience.DTO.request;

import com.senai.experience.entities.role.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "Dados para cadastro ou atualização de um usuário")
@Data
public class UsuarioRequest {

    @Schema(description = "Nome completo do usuário", example = "Maria Silva", minLength = 2, maxLength = 100)
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Schema(description = "E-mail do usuário (usado como login)", example = "maria.silva@email.com")
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    @Schema(
        description = "Senha do usuário. Mínimo 8 caracteres, ao menos 1 letra, 1 número e 1 caractere especial (@$!%*#?&)",
        example = "Senha@123"
    )
    @NotBlank(message = "A senha é obrigatória")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "A senha deve ter no mínimo 8 caracteres, ao menos 1 letra, 1 número e 1 caractere especial (@$!%*#?&)"
    )
    private String senha;

    @Schema(description = "Data de nascimento do usuário (deve ser uma data passada)", example = "1990-05-20")
    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve ser uma data passada")
    private LocalDate dataNascimento;

    @Schema(description = "Perfil de acesso do usuário", example = "CLIENTE", allowableValues = {"ADMIN", "VENDEDOR", "CLIENTE", "IOT"})
    @NotNull(message = "O perfil é obrigatório")
    private UserRole role;
}
