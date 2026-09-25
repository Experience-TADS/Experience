package com.senai.experience.repositories;

import com.senai.experience.entities.SessaoAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SessaoAnalyticsRepository extends JpaRepository<SessaoAnalytics, Long> {

    @Query("SELECT s.secao, AVG(s.duracaoSegundos), COUNT(s) FROM SessaoAnalytics s GROUP BY s.secao")
    List<Object[]> findResumoBySecao();
}
