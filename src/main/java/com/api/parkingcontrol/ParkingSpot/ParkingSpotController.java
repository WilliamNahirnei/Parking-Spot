package com.api.parkingcontrol.ParkingSpot;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotDTO));
    }

    @GetMapping
    public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpots(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getParkingSpotById(@PathVariable(value = "id") UUID id){
        Optional <ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
    }

//    @PutMapping("/{id}")
//    ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id,
//                                             @RequestBody @Valid ParkingSpotDTO parkingSpotDTO){
//        Optional <ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
//        if (!parkingSpotModelOptional.isPresent())
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
//        var parkingSpotModel = new ParkingSpotModel();
//        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);
//        parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
//        parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());
//
//        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") UUID id){
        Optional <ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
        parkingSpotService.delete(parkingSpotModelOptional.get());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Parking Spot deleted with Successfully");
    }
}
