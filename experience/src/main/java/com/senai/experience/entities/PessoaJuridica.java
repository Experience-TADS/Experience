package com.senai.experience.entities;


@Entity
@Column(name = "pessoa_juridica")
@Data
public class PessoaJuridica extends Usuario {
    private String cnpj;
    private String razaoSocial;

    public PessoaJuridica() {
        super();
    }

    public PessoaJuridica(String nome, String email, String telefone, String cnpj, String razaoSocial) {
        super(nome, email, telefone);
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
    }
}