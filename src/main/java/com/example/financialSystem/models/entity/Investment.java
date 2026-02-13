package com.example.financialSystem.models.entity;

import com.example.financialSystem.models.enums.InvestmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Investment extends Financial {
    @Enumerated(EnumType.STRING)
    private InvestmentType investmentType;

    private int actionQuantity;

    @Transient
    private BigDecimal currentValue;

    private String brokerName;

}