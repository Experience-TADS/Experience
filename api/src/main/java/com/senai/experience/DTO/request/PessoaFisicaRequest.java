package com.senai.experience.DTO.request;

import com.senai.experience.entities.role.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "Dados para cadastro de um usuário do tipo pessoa física")
@Data
public class PessoaFisicaRequest {

    @Schema(description = "Nome completo", example = "Ana Souza", minLength = 2, maxLength = 100)
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Schema(description = "E-mail (usado como login)", example = "ana.souza@email.com")
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    @Schema(
        description = "Senha. Mínimo 8 caracteres, ao menos 1 letra, 1 número e 1 caractere especial",
        example = "Senha@123"
    )
    @NotBlank(message = "A senha é obrigatória")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "A senha deve ter no mínimo 8 caracteres, ao menos 1 letra, 1 número e 1 caractere especial (@$!%*#?&)"
    )
    private String senha;

    @Schema(description = "Data de nascimento (deve ser uma data passada)", example = "1990-03-15")
    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve ser uma data passada")
    private LocalDate dataNascimento;

    @Schema(description = "CPF com 11 dígitos numéricos sem formatação", example = "52998224725")
    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
    private String cpf;

    @Schema(description = "Perfil de acesso", example = "CLIENTE", allowableValues = {"ADMIN", "VENDEDOR", "CLIENTE", "IOT"})
    @NotNull(message = "O perfil é obrigatório")
    private UserRole role;
}
