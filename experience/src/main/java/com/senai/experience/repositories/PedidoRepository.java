package com.senai.experience.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.senai.experience.entities.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}