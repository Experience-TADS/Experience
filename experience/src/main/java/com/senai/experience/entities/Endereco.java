package com.senai.experience.entities; //

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter //getter para todos os atributos
@Setter //setter para todos os atributos
@AllArgsConstructor //construtor com todos os argumentos
@NoArgsConstructor //construtor sem argumentos
@Entity
@Table(name = "tb_endereco")
public class Endereco
{
    //ATRIBUTOS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;          // use Long for JPA primary key
    
    private String cep;
    private String logradouro;
    private int numero;
    private String bairro;
    private String cidade;
    private String estado;

    @ManyToOne
    @JoinColumn(name="id_usuario")
    private Usuario usuario;

    // TO STRING
    @Override
    public String toString() {
        return "Endereco{" +
                "id=" + id +
                ", cep='" + cep + '\'' +
                ", logradouro='" + logradouro + '\'' +
                ", numero=" + numero +
                ", bairro='" + bairro + '\'' +
                ", cidade='" + cidade + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }

    @JsonCreator
    public Endereco(@JsonProperty("id_usuario") Long id_usuario) {
        this.usuario = new Usuario();
        this.usuario.setId(id_usuario);
    }
}