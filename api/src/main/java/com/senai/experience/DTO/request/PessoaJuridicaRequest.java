package com.senai.experience.DTO.request;

import com.senai.experience.entities.role.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.br.CNPJ;

import java.time.LocalDate;

@Schema(description = "Dados para cadastro de um usuário do tipo pessoa jurídica")
@Data
public class PessoaJuridicaRequest {

    @Schema(description = "Nome do responsável ou representante", example = "Carlos Santos", minLength = 2, maxLength = 100)
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Schema(description = "E-mail corporativo (usado como login)", example = "empresa@cnpj.com.br")
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    @Schema(
        description = "Senha. Mínimo 8 caracteres, ao menos 1 letra, 1 número e 1 caractere especial",
        example = "Empresa@2024"
    )
    @NotBlank(message = "A senha é obrigatória")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "A senha deve ter no mínimo 8 caracteres, ao menos 1 letra, 1 número e 1 caractere especial (@$!%*#?&)"
    )
    private String senha;

    @Schema(description = "Data de nascimento do responsável (deve ser uma data passada)", example = "1980-07-10")
    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve ser uma data passada")
    private LocalDate dataNascimento;

    @Schema(description = "CNPJ com 14 dígitos numéricos sem formatação", example = "12345678000195")
    @NotBlank(message = "O CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter exatamente 14 dígitos numéricos")
    // Valida os dígitos verificadores do CNPJ (rejeita valores como 11111111111111).
    // Fica no DTO para que a falha vire 400 via @Valid, em vez de estourar só na
    // persistência (onde o handler genérico a transformava em 404).
    @CNPJ(message = "CNPJ inválido")
    private String cnpj;

    @Schema(description = "Razão social da empresa", example = "Empresa de Veículos LTDA", minLength = 2, maxLength = 200)
    @NotBlank(message = "A razão social é obrigatória")
    @Size(min = 2, max = 200, message = "A razão social deve ter entre 2 e 200 caracteres")
    private String razaoSocial;

    @Schema(description = "Perfil de acesso", example = "CLIENTE", allowableValues = {"ADMIN", "VENDEDOR", "CLIENTE", "IOT"})
    @NotNull(message = "O perfil é obrigatório")
    private UserRole role;
}
