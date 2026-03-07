package com.senai.experience.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 100, message = "O nome deve conter entre 2 e 100 caracteres")
    private String nome;

    @Email
    @NotNull
    @NotBlank
    private String email;

    @NotBlank
    @NotNull
    @Size(min = 3, message = "A senha deve conter no mínimo 3 caracteres")
    private String senhaHash;

    @NotNull
    @NotBlank
    private LocalDate dataNascimento;
}