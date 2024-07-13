package ar.edu.utn.frc.tup.lc.iii.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "runners")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RunnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
