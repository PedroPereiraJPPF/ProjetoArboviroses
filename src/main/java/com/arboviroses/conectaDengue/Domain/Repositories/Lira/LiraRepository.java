package com.arboviroses.conectaDengue.Domain.Repositories.Lira;

import com.arboviroses.conectaDengue.Domain.Entities.Lira.Lira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiraRepository extends JpaRepository<Lira, Long> {
    List<Lira> findByAno(Integer ano);
    List<Lira> findByBairroAndAno(String bairro, Integer ano);
    List<Lira> findByAnoAndLiraNumber(Integer ano, Integer liraNumber);
}
