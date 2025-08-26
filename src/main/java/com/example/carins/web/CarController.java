package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.service.CarService;
import com.example.carins.service.ClaimService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.CarHistoryEvent;
import com.example.carins.web.dto.ClaimDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarService service;
    private final ClaimService claimService;
    public CarController(CarService service, ClaimService claimService) {
        this.service = service;
        this.claimService = claimService;

    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId, @RequestParam String date) {
        LocalDate d;
        try {
             d = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of("error","Invalid date format. Expected YYYY-MM-DD"));
        }

        if(d.isBefore(LocalDate.of(1900,1,1)) || d.isAfter(LocalDate.of(2100,12,31))) {
            return ResponseEntity.badRequest().body(Map.of("error","Date must be between 1900 and 2100"));
        }
        try {
            boolean valid = service.isInsuranceValid(carId, d);
            return ResponseEntity.ok(new InsuranceValidityResponse(carId, d.toString(),valid));
        } catch(NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    private CarDto toDto(Car c) {
        var o = c.getOwner();
        return new CarDto(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }

    @PostMapping("/cars/{carId}/claims")
    public ResponseEntity<ClaimDto> createClaim(@PathVariable Long carId, @RequestBody @Valid Claim claim) {
        Claim savedClaim = claimService.createClaim(carId, claim);
        URI location = URI.create("/api/cars/"+carId+"/claims"+savedClaim.getId());
        ClaimDto dto = new ClaimDto(
          savedClaim.getId(),
          carId,
          savedClaim.getClaimDate(),
          savedClaim.getDescription(),
          savedClaim.getAmount()
        );
        return ResponseEntity.created(location).body(dto);
    }

    @GetMapping("/cars/{carId}/history")
    public ResponseEntity<List<CarHistoryEvent>> getCartHistory(@PathVariable Long carId) {
        List<CarHistoryEvent> history = service.getCarHistory(carId);
        return ResponseEntity.ok(history);
    }

    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {}
}
