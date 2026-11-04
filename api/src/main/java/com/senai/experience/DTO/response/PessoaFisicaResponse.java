package com.senai.experience.DTO.response;

import com.senai.experience.entities.role.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import lombok.Data;

@Schema(description = "Dados de uma pessoa física retornados pela API")
@Data
public class PessoaFisicaResponse {

    @Schema(description = "Identificador único", example = "1")
    private Long id;

    @Schema(description = "Nome completo", example = "Ana Souza")
    private String nome;

    @Schema(description = "E-mail", example = "ana.souza@email.com")
    private String email;

    @Schema(description = "Data de nascimento", example = "1990-03-15")
    private LocalDate dataNascimento;

    @Schema(description = "CPF com 11 dígitos", example = "52998224725")
    private String cpf;

    @Schema(description = "Perfil de acesso", example = "CLIENTE")
    private UserRole role;

    @Schema(description = "Indica se a conta está ativa", example = "true")
    private boolean ativo;
}
