package com.senai.experience.DTO.response;

import com.senai.experience.entities.role.UserRole;

import java.time.LocalDate;
import lombok.Data;

@Data
public class PessoaFisicaResponse {
    private Long id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String cpf;
    private UserRole role;
    private boolean ativo;
}
