package com.senai.experience.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;   
import lombok.Getter;
import lombok.NoArgsConstructor;    
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;
@Getter //getter para todos os atributos
@Setter //setter para todos os atributos
@AllArgsConstructor //construtor com todos os argumentos  
@NoArgsConstructor //construtor sem argumentos
@Entity
@Table(name = "tb_pedido")
public class Pedido {
    //ATRIBUTOS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // use Long for JPA primary key

    private int idCliente;
    private int idVendedor;
    private LocalDateTime dataPedido;
    private BigDecimal valorTotal;
    

    //TO STRING
    @Override   
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", idCliente=" + idCliente +
                ", idVendedor=" + idVendedor +
                ", dataPedido=" + dataPedido +
                '}';
    }
}