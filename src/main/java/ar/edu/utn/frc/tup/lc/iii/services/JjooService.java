package ar.edu.utn.frc.tup.lc.iii.services;

import ar.edu.utn.frc.tup.lc.iii.dtos.LaneTimeDTO;
import ar.edu.utn.frc.tup.lc.iii.models.Heat;
import ar.edu.utn.frc.tup.lc.iii.models.Runner;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface JjooService {

    List<Heat> startEvent() throws BadRequestException;


    List<Heat> registerTimes(Integer heatId, List<LaneTimeDTO> times) throws BadRequestException;

    List<Heat> getAllHeatsOrdered();
}
