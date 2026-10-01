package com.senai.experience.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.senai.experience.entities.Agenda;
import com.senai.experience.entities.Usuario;
import com.senai.experience.entities.agenda.StatusAgenda;
import com.senai.experience.entities.agenda.TipoAgenda;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByColaborador(Usuario colaborador);

    List<Agenda> findByCliente(Usuario cliente);

    @Query("""
        SELECT COUNT(a) > 0 FROM Agenda a
        WHERE a.colaborador.id = :colaboradorId
          AND a.dataHora = :dataHora
          AND a.status <> :cancelado
          AND (:excludeId IS NULL OR a.id <> :excludeId)
    """)
    boolean existeConflito(
        @Param("colaboradorId") Long colaboradorId,
        @Param("dataHora") LocalDateTime dataHora,
        @Param("cancelado") StatusAgenda cancelado,
        @Param("excludeId") Long excludeId
    );

    @Query("""
        SELECT a.dataHora FROM Agenda a
        WHERE a.colaborador.id = :colaboradorId
          AND a.status <> :cancelado
          AND a.dataHora >= :inicio
    """)
    List<LocalDateTime> findHorariosOcupados(
        @Param("colaboradorId") Long colaboradorId,
        @Param("cancelado") StatusAgenda cancelado,
        @Param("inicio") LocalDateTime inicio
    );

    boolean existsByColaboradorAndClienteAndTipoAndStatus(
        Usuario colaborador, Usuario cliente, TipoAgenda tipo, StatusAgenda status
    );
}
