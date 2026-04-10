package com.senai.experience.repositories;

    import org.springframework.data.jpa.repository.JpaRepository;
    import com.senai.experience.entities.Veiculo;
    
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    
}
