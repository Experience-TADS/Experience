package com.senai.experience.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Entity
@Table(name = "pessoa_fisica")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class PessoaFisica extends Usuario{


    @CPF(message = "O formato do CPF é inválido.")
    @NotBlank(message = "O CPF não pode estar em branco.")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    public PessoaFisica() {
        super();
    }

    public PessoaFisica(String nome, String email, String senhaHash, String cpf, LocalDate dataNascimento) {
        super(nome, email, senhaHash, dataNascimento);
        this.cpf = cpf;
    }
}