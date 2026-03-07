package com.senai.experience.entities; //

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter //getter para todos os atributos
@Setter //setter para todos os atributos
@AllArgsConstructor //construtor com todos os argumentos  
@NoArgsConstructor //construtor sem argumentos

public class Telefone{

    //ATRIBUTOS
    public Long id; // use Long for JPA primary key
    public int numero;

    //TO STRING
    public String toString() {
        return "Telefone: " + numero +
                "Id: " + id;
    }
}