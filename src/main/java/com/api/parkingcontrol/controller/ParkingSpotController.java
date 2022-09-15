package com.api.parkingcontrol.controller;

import com.api.parkingcontrol.DTOs.ParkingSpotDTO;
import com.api.parkingcontrol.model.ParkingSpotModel;
import com.api.parkingcontrol.service.ParkingSpotService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {
    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping
    public ResponseEntity<Object> saveSpot(@RequestBody @Valid ParkingSpotDTO parkingSpotDTO) {

        if (parkingSpotService.existsByLicensePlateCar(parkingSpotDTO.getLicensePlateCar()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License plate car is already in use");
        if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking spot is already in use");
        if (parkingSpotService.existsByApartmentAndBlock(parkingSpotDTO.getApartment(), parkingSpotDTO.getBlock()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking spot already registered in this apartment and block");

        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }

    @GetMapping
    public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots(){
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getParkingSpotById(@PathVariable(value = "id") UUID id){
        Optional <ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
    }
}
