package com.senai.experience.DTO.response;

import com.senai.experience.entities.role.UserRole;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;       
    private LocalDate dataNascimento;
    private UserRole role;
}
