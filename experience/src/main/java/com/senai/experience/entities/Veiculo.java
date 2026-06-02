package com.senai.experience.entities;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_veiculo")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;

    private int chassi;

    @Enumerated(EnumType.STRING)
    private StatusFabricacao statusVeiculo;

    @Column(name = "id_pedido")
    private Long idPedido;

    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<StatusHistorico> historico;

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
