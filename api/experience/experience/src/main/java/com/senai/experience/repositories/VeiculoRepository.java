package com.senai.experience.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.senai.experience.entities.Veiculo;

import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    Veiculo findByChassi(int chassi);

    /**
     * Busca o veículo vinculado a um pedido específico.
     * Usado pelo DashboardService para montar a timeline do app mobile.
     */
    Optional<Veiculo> findByIdPedido(Long idPedido);
}
