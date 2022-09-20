package com.api.parkingcontrol.ParkingSpot;

import com.api.parkingcontrol.utils.exception.ValidationException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParkingSpotService {

    final com.api.parkingcontrol.ParkingSpot.ParkingSpotRepository parkingSpotRepository;

    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    @Transactional
    public ParkingSpotModel save(ParkingSpotDTO parkingSpotDTO) throws Exception {
        this.validateUniqueAttributes(parkingSpotDTO);
        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return parkingSpotRepository.save(parkingSpotModel);
    }

    private void validateUniqueAttributes(ParkingSpotDTO parkingSpotDTO) throws ValidationException {
        if (this.existsByLicensePlateCar(parkingSpotDTO.getLicensePlateCar()))
            throw new ValidationException("Conflict: License plate car is alert in use", 409, "Conflict: License plate car is alert in use");
        if (this.existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber()))
            throw new ValidationException("Conflict: Parking spot is already in use", 409, "Conflict: Parking spot is already in use");
        if (this.existsByApartmentAndBlock(parkingSpotDTO.getApartment(), parkingSpotDTO.getBlock()))
            throw new ValidationException("Conflict: Parking spot already registered in this apartment and block", 409, "Conflict: Parking spot already registered in this apartment and block");
    }

    public  boolean existsByLicensePlateCar(String licensePlateCar) {
        return parkingSpotRepository.existsByLicensePlateCar(licensePlateCar);
    }

    public boolean existsByParkingSpotNumber(String parkingSpotNumber) {
        return parkingSpotRepository.existsByParkingSpotNumber(parkingSpotNumber);
    }

    public boolean existsByApartmentAndBlock(String apartment, String block) {
        return parkingSpotRepository.existsByApartmentAndBlock(apartment, block);
    }

    public Page<ParkingSpotModel> findAll(Pageable pageable) {
        return parkingSpotRepository.findAll(pageable);
    }

    public ParkingSpotModel findById(UUID id) throws ValidationException {
        Optional <ParkingSpotModel> parkingSpotModelOptional = parkingSpotRepository.findById(id);
        if (!parkingSpotModelOptional.isPresent())
            throw new ValidationException("Parking Spot not found", HttpStatus.NOT_FOUND.value(), "Parking Spot not Found");
        return parkingSpotModelOptional.get();
    }

    @Transactional
    public void delete(UUID id) throws ValidationException {
        ParkingSpotModel parkingSpot = this.findById(id);
        parkingSpotRepository.delete(parkingSpot);
    }
}
