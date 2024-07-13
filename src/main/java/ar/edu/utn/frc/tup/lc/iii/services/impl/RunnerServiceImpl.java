package ar.edu.utn.frc.tup.lc.iii.services.impl;

import ar.edu.utn.frc.tup.lc.iii.models.Runner;
import ar.edu.utn.frc.tup.lc.iii.repositories.RunnerRepository;
import ar.edu.utn.frc.tup.lc.iii.services.RunnerService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RunnerServiceImpl implements RunnerService {

    @Autowired
    private RunnerRepository runnerRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public List<Runner> getAllRunners() {
        return modelMapper.map(runnerRepository.findAll(), new TypeToken<List<Runner>>() {
        }.getType());
    }

    @Override
    public Runner getRunnerById(Long id) {
        return modelMapper.map(runnerRepository.findById(id).orElse(null), Runner.class);
    }
}
