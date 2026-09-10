package com.senai.experience.DTO.request;

import com.senai.experience.entities.role.UserRole;
import java.time.LocalDate;
import lombok.Data;

@Data
public class PessoaFisicaRequest {
    private String nome;
    private String email;
    private String senha;
    private LocalDate dataNascimento;
    private String cpf;
    private UserRole role;
}
