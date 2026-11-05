package com.senai.experience.DTO.response;

import com.senai.experience.entities.role.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

@Schema(description = "Dados de uma pessoa jurídica retornados pela API")
@Data
public class PessoaJuridicaResponse {

    @Schema(description = "Identificador único", example = "1")
    private Long id;

    @Schema(description = "Nome do responsável", example = "Carlos Santos")
    private String nome;

    @Schema(description = "E-mail corporativo", example = "empresa@cnpj.com.br")
    private String email;

    @Schema(description = "Data de nascimento do responsável", example = "1980-07-10")
    private LocalDate dataNascimento;

    @Schema(description = "CNPJ com 14 dígitos", example = "12345678000195")
    private String cnpj;

    @Schema(description = "Razão social da empresa", example = "Empresa de Veículos LTDA")
    private String razaoSocial;

    @Schema(description = "Perfil de acesso", example = "CLIENTE")
    private UserRole role;

    @Schema(description = "Indica se a conta está ativa", example = "true")
    private boolean ativo;
}
