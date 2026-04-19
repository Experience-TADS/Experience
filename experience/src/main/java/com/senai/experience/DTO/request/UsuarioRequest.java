package com.senai.experience.DTO.request;

import com.senai.experience.entities.role.UserRole;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioRequest {
    private String nome;
    private String email;
    private String senha;       
    private LocalDate dataNascimento;
    private UserRole role;
}
