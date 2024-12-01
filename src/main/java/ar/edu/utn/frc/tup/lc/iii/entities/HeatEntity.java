package ar.edu.utn.frc.tup.lc.iii.entities;

import ar.edu.utn.frc.tup.lc.iii.enums.PhaseEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "heats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private PhaseEnum phase;


    private Integer lane;

    private Integer heat;


    @ManyToOne
    @JoinColumn(name = "runner_id")
    private RunnerEntity runner;

    private Long time;

    private Boolean passedToNextPhase = false;


}
