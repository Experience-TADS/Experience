package com.senai.experience.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;

import java.time.LocalDate;

@Entity
@Table(name = "pessoa_juridica")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class PessoaJuridica extends Usuario {

    @CNPJ(message = "O formato do CNPJ é inválido.")
    @NotBlank(message = "O CNPJ não pode estar em branco.")
    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @NotBlank(message = "A Razão Social não pode estar em branco.")
    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;

    public PessoaJuridica() {
        super();
    }

    // Construtor ajustado para corresponder à superclasse Usuario
    public PessoaJuridica(String nome, String email, String senhaHash, LocalDate dataNascimento, String cnpj, String razaoSocial) {
        super(nome, email, senhaHash, dataNascimento);
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
    }
}