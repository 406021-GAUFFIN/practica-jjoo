package ar.edu.utn.frc.tup.lc.iii.controllers;

import ar.edu.utn.frc.tup.lc.iii.dtos.HeatDTO;
import ar.edu.utn.frc.tup.lc.iii.dtos.LaneTimeDTO;
import ar.edu.utn.frc.tup.lc.iii.models.Heat;
import ar.edu.utn.frc.tup.lc.iii.services.JjooService;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class JjooController {

    private final JjooService jjooService;


    @PostMapping("/start-event")
    public ResponseEntity<List<Heat>> startEvent() throws BadRequestException {
        return ResponseEntity.ok(jjooService.startEvent());
    }

    @PutMapping("/heat/{heatId}/times")
    public ResponseEntity<List<Heat>> registerTimes(@PathVariable Integer heatId, @RequestBody List<LaneTimeDTO> times) throws BadRequestException {
        return ResponseEntity.ok(jjooService.registerTimes(heatId, times));
    }

    @GetMapping("/heat")
    public ResponseEntity<List<Heat>> getAllHeats()  {
        return ResponseEntity.ok(jjooService.getAllHeatsOrdered());
    }
}
