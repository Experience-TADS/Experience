package com.senai.experience.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
    private String senhaHash;

    @NotNull
    private LocalDate dataNascimento;

    // Este é o construtor manual que as classes filhas (PF e PJ) chamam via super()
    public Usuario(String nome, String email, String senhaHash, LocalDate dataNascimento) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.dataNascimento = dataNascimento;
    }



    // Getters e Setters explícitos para garantir compilação
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // Outros getters podem ser adicionados conforme necessidade, mas o ID é crítico para os controladores
}