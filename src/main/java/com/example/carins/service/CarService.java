package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.ClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.CarHistoryEvent;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final ClaimRepository claimRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository, ClaimRepository claimRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        carRepository.findById(carId)
                .orElseThrow(()->new NoSuchElementException("Car with id " + carId + " not found"));
        return policyRepository.existsActiveOnDate(carId, date);
    }
    public Car getCarById(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(()->new NoSuchElementException("Car not found: " + carId));
    }

    public List<CarHistoryEvent> getCarHistory(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NoSuchElementException("Car not found: " + carId));
        List<CarHistoryEvent> events = new ArrayList<>();

        policyRepository.findByCarId(carId).forEach( policy ->
                events.add(new CarHistoryEvent(
                        policy.getStartDate(),
                        "Insurance",
                        "Insurance policy active"
                ))
        );

        claimRepository.findByCarId(carId).forEach(claim ->
                events.add(new CarHistoryEvent(
                        claim.getClaimDate(),
                        "claim",
                        "Insurance claim filed"
                ))
        );
        return events.stream().sorted(Comparator.comparing(CarHistoryEvent::date))
                .collect(Collectors.toList());
    }
}
