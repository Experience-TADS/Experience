package com.senai.experience.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "pessoa_fisica")
@Data
@EqualsAndHashCode(callSuper = true)
public class PessoaFisica extends Usuario{


    @Column(nullable = false, unique = true)
    private String cpf;

    public PessoaFisica() {
        super();
    }

    public PessoaFisica(String nome, String email, String cpf, LocalDate dataNascimento) {
        super(nome, email, dataNascimento);
        this.cpf = cpf;
    }
}