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
    
    @Column(length = 11, nullable = false) // Ex: 11987654321
    private String numero;

    /**
     * Formata o número de telefone para exibição.
     * Ex: (11) 98765-4321 ou (11) 8765-4321
     * @return O número formatado.
     */
    public String getNumeroFormatado() {
        if (numero == null) return "";
        if (numero.length() == 11) { // Celular com 9
            return numero.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }
        if (numero.length() == 10) { // Fixo ou celular sem 9
            return numero.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        return numero; // Retorna o número como está se não se encaixar nos padrões
    }

    //TO STRING
    @Override
    public String toString() {
        return "Telefone{id=" + id + ", numero='" + getNumeroFormatado() + "'}";
    }
}