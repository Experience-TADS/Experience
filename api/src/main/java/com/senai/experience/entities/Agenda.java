package com.senai.experience.entities;

import java.time.LocalDateTime;

import com.senai.experience.entities.agenda.StatusAgenda;
import com.senai.experience.entities.agenda.TipoAgenda;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "agenda")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Usuario colaborador;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAgenda tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgenda status;

    private String observacao;
}
