package ar.edu.utn.frc.tup.lc.iii.services;

import ar.edu.utn.frc.tup.lc.iii.models.Runner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RunnerService {

    List<Runner> getAllRunners();

    Runner getRunnerById(Long id);
}
