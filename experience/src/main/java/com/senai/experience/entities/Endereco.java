package com.senai.experience.entities; //

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter //getter para todos os atributos
@Setter //setter para todos os atributos
@AllArgsConstructor //construtor com todos os argumentos  
@NoArgsConstructor //construtor sem argumentos
//@Data //gera toString, equals e hashCode

public class Endereco
{
    //ATRIBUTOS
    public Long id;          // use Long for JPA primary key
    public String cep;
    public String logradouro;
    public int numero;
    public String bairro;
    public String cidade;
    public String estado;

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
}