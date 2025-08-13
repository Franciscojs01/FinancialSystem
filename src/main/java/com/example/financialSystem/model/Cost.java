package com.example.financialSystem.model;

import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Cost extends Financial {
    private String observation;

    public Cost(String type, BigDecimal value, LocalDate dateFinancial, User user, String observation) {
        super(type, value, dateFinancial, user);
        this.observation = observation;
    }

    public Cost() {}

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
