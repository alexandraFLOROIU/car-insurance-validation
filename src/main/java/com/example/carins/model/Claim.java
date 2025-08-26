package com.example.carins.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "claim")
public class Claim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate claimDate;

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin(value = "0.01",  message = "Amount must be greater than zero")
    private BigDecimal amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Car car;

    public Claim() {}

    public Claim(Car car, LocalDate claimDate, String description, BigDecimal amount) {
        this.car = car;
        this.claimDate = claimDate;
        this.description = description;
        this.amount = amount;
    }

   public Long getId() {return id;}
   public LocalDate getClaimDate() { return claimDate;}
   public void setClaimDate(LocalDate claimDate) { this.claimDate = claimDate;}
   public String getDescription() { return description;}
   public void setDescription(String description) { this.description = description;}
   public BigDecimal getAmount() { return amount;}
   public void setAmount(BigDecimal amount) { this.amount = amount;}
   public Car getCar() { return car; }
   public void setCar(Car car) {this.car = car;}

}
