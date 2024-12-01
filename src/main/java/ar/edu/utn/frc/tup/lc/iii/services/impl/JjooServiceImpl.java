package ar.edu.utn.frc.tup.lc.iii.services.impl;

import ar.edu.utn.frc.tup.lc.iii.dtos.LaneTimeDTO;
import ar.edu.utn.frc.tup.lc.iii.entities.HeatEntity;
import ar.edu.utn.frc.tup.lc.iii.entities.RunnerEntity;
import ar.edu.utn.frc.tup.lc.iii.enums.PhaseEnum;
import ar.edu.utn.frc.tup.lc.iii.models.Heat;
import ar.edu.utn.frc.tup.lc.iii.models.Runner;
import ar.edu.utn.frc.tup.lc.iii.repositories.HeatRepository;
import ar.edu.utn.frc.tup.lc.iii.repositories.RunnerRepository;
import ar.edu.utn.frc.tup.lc.iii.services.JjooService;
import ar.edu.utn.frc.tup.lc.iii.services.RunnerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.MethodNotAllowedException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JjooServiceImpl implements JjooService {



    private final HeatRepository heatRepository;
    private final RunnerRepository runnerRepository;

    private final RunnerService runnerService;



    private final ModelMapper modelMapper;


    @Override
    public List<Heat> startEvent() throws BadRequestException {

        List<HeatEntity> existingHeats = heatRepository.findAll();

        if(!existingHeats.isEmpty()){
            throw new BadRequestException("Event already started");
        }
        List<RunnerEntity> runnerEntities = runnerRepository.findAll();

        List<HeatEntity> heatEntities = new ArrayList<>();

        Collections.shuffle(runnerEntities);

        //Creo las sprimers rondas

        for(int i=1; i<=7; i++){

            for(int j=0; j<9; j++){
                HeatEntity heatEntity = new HeatEntity();

                heatEntity.setLane(j);
                heatEntity.setHeat(i);
                heatEntity.setPhase(PhaseEnum.FIRST_ROUND);
                heatEntity.setRunner(runnerEntities.remove(runnerEntities.size()-1));

                heatEntities.add(heatEntity);

            }

        }


        for(int i=8; i<=9; i++){

            for(int j=0; j<8; j++){
                HeatEntity heatEntity = new HeatEntity();

                heatEntity.setLane(j);
                heatEntity.setHeat(i);
                heatEntity.setPhase(PhaseEnum.FIRST_ROUND);
                heatEntity.setRunner(runnerEntities.remove(runnerEntities.size()-1));

                heatEntities.add(heatEntity);

            }

        }



//creo las semifinales
        for(int i=10; i<=12; i++){

            for(int j=0; j<8; j++){
                HeatEntity heatEntity = new HeatEntity();

                heatEntity.setLane(j);
                heatEntity.setHeat(i);
                heatEntity.setPhase(PhaseEnum.SEMIFINAL);
                heatEntities.add(heatEntity);

            }

        }

//creo la final

        for(int j=0; j<8; j++){
            HeatEntity heatEntity = new HeatEntity();

            heatEntity.setLane(j);
            heatEntity.setHeat(13);
            heatEntity.setPhase(PhaseEnum.FINAL);
            heatEntities.add(heatEntity);

        }

        List<Heat> result = new ArrayList<>();

        for (HeatEntity heatEntity: heatRepository.saveAll(heatEntities)){
            result.add(modelMapper.map(heatEntity, Heat.class));
        }


        return result;
    }

    @Override
    public List<Heat> registerTimes(Integer heatId, List<LaneTimeDTO> times) throws BadRequestException {
        List<HeatEntity> heats = heatRepository.findAllByHeat(heatId);

        PhaseEnum currentPhase = heats.get(0).getPhase();

        //Validaciones iniciales. Si no encuentro ningun heat. Si ya los tiempos estan cargados y si o me mandaron todos los tiempos para es eheat
        if(heats.isEmpty()){
            throw new EntityNotFoundException("Heat not found");
        }

        if(heats.get(0).getTime() != null){
            throw new BadRequestException("Heat already has times loaded");
        }

        if(heats.size()!= times.size()){
            throw new BadRequestException("All times should be loaded together");

        }

        //me armo un mapa de los heats de la db, para hacer mas facil la busqueda por lane
        Map<Integer, HeatEntity> heatsMap = heats.stream().collect(Collectors.toMap(HeatEntity::getLane, Function.identity()));

        //Obtengo todos los heats de la siguiente fase segun la fase actual, para poder pasar a los runners que corresponda
        List<HeatEntity> nextHeats = new ArrayList<>();

        if(currentPhase.equals(PhaseEnum.FIRST_ROUND)){
            nextHeats = heatRepository.findAllByPhase(PhaseEnum.SEMIFINAL);
        }

        if(currentPhase.equals(PhaseEnum.SEMIFINAL)){
            nextHeats = heatRepository.findAllByPhase(PhaseEnum.FINAL);
        }

        //Ordeno los tiempos que mandaron de mayor a menor
            times.sort(Comparator.comparing(LaneTimeDTO::getTime));

        //recorro los tiempos enviados. Busco su lane. si no lo encuentro error. Le seteo el tiempo enviado
        //si está entre los dos primeros y hay una siguiente fase, entonces lo paso hasta la siguiente fase
        for (int i=0; i < times.size(); i++){

            HeatEntity heatOfRunner = heatsMap.get(times.get(i).getLane());

            if(heatOfRunner == null){
                throw  new EntityNotFoundException("Lane not found in heat");
            }

            heatOfRunner.setTime(times.get(i).getTime());


            if(i<2 && !nextHeats.isEmpty()){
                heatOfRunner.setPassedToNextPhase(true);
                assignRunnerToEmptyLaneInHeat(heatOfRunner.getRunner(), nextHeats);
            }

        }

        //guardo todos los heats que me mandaron en esta vuelta (todavia no guardo los next phase)
        heatRepository.saveAll(heatsMap.values());

        //primero busco todos los heats de la phase actual
        List<HeatEntity> allHeatsOfCurrentPhase = heatRepository.findAllByPhase(currentPhase);

        //si todos los tiempos fueron cargados (inlcuyendo al carga actual) me saco de encima los que ya pasaron a la siguiente fase y los ordeno
        //siempre que haya una siguiente fase (si estamos en la final, no hay tal cosa)
        if(allTimesLoaded(allHeatsOfCurrentPhase) && !nextHeats.isEmpty() ){
            allHeatsOfCurrentPhase = allHeatsOfCurrentPhase.stream().filter(heat -> !heat.getPassedToNextPhase()).collect(Collectors.toList());

            allHeatsOfCurrentPhase.sort(Comparator.comparing(HeatEntity::getTime));

            //defino la cantidad de runners que pasan por tiempo segun la fase

            int amountOfRunnersForWildCarts = 0;

            if(currentPhase.equals(PhaseEnum.FIRST_ROUND)){
                amountOfRunnersForWildCarts =6;
            }
            if(currentPhase.equals(PhaseEnum.SEMIFINAL)){
                amountOfRunnersForWildCarts =2;
            }

            //tomo los primeros n runners que pasan. los asigno a un lane disponible de la siguiente fase y los marco como que pasaron
            for(int i=0; i<amountOfRunnersForWildCarts; i++){
                assignRunnerToEmptyLaneInHeat(allHeatsOfCurrentPhase.get(i).getRunner(),nextHeats);
                allHeatsOfCurrentPhase.get(i).setPassedToNextPhase(true);
            }

            //guardo los heats actuales de nuevo y guardo los heats de la siguiente fase
            heatRepository.saveAll(allHeatsOfCurrentPhase);
            heatRepository.saveAll((nextHeats));



        }


        //al final me traigo todos de nuevo para devolver al front completo
        List<HeatEntity> allHeatsEntity = heatRepository.findAll();

        List<Heat> allHeatsResult = new ArrayList<>();

        for(HeatEntity heatEntity: allHeatsEntity){
            allHeatsResult.add(modelMapper.map(heatEntity, Heat.class));
        }

        return allHeatsResult;



    }

    @Override
    public List<Heat> getAllHeatsOrdered() {
        List<HeatEntity> allHeats = new ArrayList<>();

        List<HeatEntity> finalHeats = heatRepository.findAllByPhase(PhaseEnum.FINAL);

        if(allTimesLoaded(finalHeats)){
            finalHeats.sort(Comparator.comparing(HeatEntity::getTime));

        }

        allHeats.addAll(finalHeats);

        List<HeatEntity> semiFinalHeats = heatRepository.findAllByPhase(PhaseEnum.SEMIFINAL);

        semiFinalHeats = semiFinalHeats.stream().filter(heat -> !heat.getPassedToNextPhase()).collect(Collectors.toList());

        if(allTimesLoaded(semiFinalHeats)){
            semiFinalHeats.sort(Comparator.comparing(HeatEntity::getTime));

        }

        allHeats.addAll(semiFinalHeats);

        List<HeatEntity> firstPhaseHeats = heatRepository.findAllByPhase(PhaseEnum.FIRST_ROUND);

        firstPhaseHeats = firstPhaseHeats.stream().filter(heat -> !heat.getPassedToNextPhase()).collect(Collectors.toList());
        if(allTimesLoaded(firstPhaseHeats)){
            firstPhaseHeats.sort(Comparator.comparing(HeatEntity::getTime));

        }

        allHeats.addAll(firstPhaseHeats);


        List<Heat> allHeatsResult = new ArrayList<>();

        for(HeatEntity heatEntity: allHeats){
            allHeatsResult.add(modelMapper.map(heatEntity, Heat.class));
        }

        return allHeatsResult;

    }


    private Boolean allTimesLoaded(List<HeatEntity> heats){

        for(HeatEntity heat: heats){
            if(heat.getTime()==null){
                return false;
            }
        }

        return true;

    }

    private void assignRunnerToEmptyLaneInHeat(RunnerEntity runner, List<HeatEntity> heats){
        for(HeatEntity heat: heats){
            if(heat.getRunner()==null){
                heat.setRunner(runner);
                return;
            }
        }
    }
}
