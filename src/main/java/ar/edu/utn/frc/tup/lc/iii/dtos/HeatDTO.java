package ar.edu.utn.frc.tup.lc.iii.dtos;

import ar.edu.utn.frc.tup.lc.iii.entities.RunnerEntity;
import ar.edu.utn.frc.tup.lc.iii.enums.PhaseEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeatDTO {


    private Long id;

    private PhaseEnum phase;


    private Integer lane;

    private RunnerEntity runner;

    private Long time;
}
