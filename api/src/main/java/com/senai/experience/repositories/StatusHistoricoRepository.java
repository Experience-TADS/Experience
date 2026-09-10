package com.senai.experience.repositories;

import com.senai.experience.entities.StatusHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StatusHistoricoRepository extends JpaRepository<StatusHistorico, Long> {
    List<StatusHistorico> findByVeiculoIdOrderByDataAlteracaoDesc(Long veiculoId);
}
