package com.senai.experience.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.senai.experience.entities.Pedido;
import com.senai.experience.entities.Usuario;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByIdCliente(Usuario cliente);
    List<Pedido> findByIdVendedor(Usuario vendedor);

    @Query("SELECT p.idCliente FROM Pedido p WHERE p.id = " +
           "(SELECT v.idPedido FROM Veiculo v WHERE v.id = :veiculoId)")
    Optional<Usuario> findClienteByVeiculoId(@Param("veiculoId") Long veiculoId);
}
