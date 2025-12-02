package com.example.financialSystem.model.entity;

import com.example.financialSystem.model.enums.InvestmentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Investment extends Financial {
    @Enumerated(EnumType.STRING)
    private InvestmentType investmentType;

    private int actionQuantity;

    @JsonIgnore
    private BigDecimal currentValue;

    private String brokerName;

    private int daysInvested;
}