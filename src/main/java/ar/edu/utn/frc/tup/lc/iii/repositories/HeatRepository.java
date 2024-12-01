package ar.edu.utn.frc.tup.lc.iii.repositories;

import ar.edu.utn.frc.tup.lc.iii.entities.HeatEntity;
import ar.edu.utn.frc.tup.lc.iii.entities.RunnerEntity;
import ar.edu.utn.frc.tup.lc.iii.enums.PhaseEnum;
import ar.edu.utn.frc.tup.lc.iii.models.Heat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeatRepository extends JpaRepository<HeatEntity, Long> {

    List<HeatEntity> findAllByHeat(Integer heat);

    List<HeatEntity> findAllByPhase(PhaseEnum phaseEnum);

}
