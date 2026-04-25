package com.senai.experience.entities;

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
@Table(name = "tb_veiculo")
public class Veiculo {
    //ATRIBUTOS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // use Long for JPA primary key

    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;

    private int chassi;

    @Enumerated(EnumType.STRING)
    private StatusFabricacao statusVeiculo;

    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private java.util.List<StatusHistorico> historico;
    

    //TO STRING
    @Override   
    public String toString() {
        return "Veiculo{" +
                "id=" + id +
                ", idProduto=" + produto +
                ", chassi=" + chassi +
                ", StatusVeiculo='" + statusVeiculo + '\'' +
                '}';
    }
}