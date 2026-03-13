package com.senai.experience.entities; //

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
@Table(name = "tb_telefone")
public class Telefone{

    //ATRIBUTOS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // use Long for JPA primary key
    
    private int numero; // Sugestão: Mudar para String futuramente para suportar formatação

    //TO STRING
    public String toString() {
        return "Telefone: " + numero +
                "Id: " + id;
    }
}