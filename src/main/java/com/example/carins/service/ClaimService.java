package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.repo.ClaimRepository;
import org.springframework.stereotype.Service;

@Service
public class ClaimService {
    private final ClaimRepository claimRepository;
    private final CarService carService;
    public ClaimService( ClaimRepository claimRepository, CarService carService) {
        this.claimRepository = claimRepository;
        this.carService = carService;
    }

    public Claim createClaim(Long carId, Claim claim) {
        Car car = carService.getCarById(carId);
        claim.setCar(car);
        return claimRepository.save(claim);
    }
}
