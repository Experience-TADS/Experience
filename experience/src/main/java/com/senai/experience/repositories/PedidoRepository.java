package com.senai.experience.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.senai.experience.entities.Pedido;
import com.senai.experience.entities.Usuario;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByIdCliente(Usuario cliente);
}