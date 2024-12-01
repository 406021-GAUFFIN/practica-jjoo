package ar.edu.utn.frc.tup.lc.iii.models;

import ar.edu.utn.frc.tup.lc.iii.entities.RunnerEntity;
import ar.edu.utn.frc.tup.lc.iii.enums.PhaseEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Heat {


    private Long id;

    private PhaseEnum phase;

    private Integer heat;


    private Integer lane;

    private RunnerEntity runner;

    private Long time;

    private Boolean passedToNextPhase;

}
