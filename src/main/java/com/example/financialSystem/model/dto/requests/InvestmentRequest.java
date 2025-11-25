package com.example.financialSystem.model.dto.requests;

import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.util.BenchMarkRate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentRequest(
        @NotNull(message = "type is required")
        InvestmentType investmentType,

        BigDecimal value,
        LocalDate dateFinancial,
        BenchMarkRate baseCurrency,

        @Min(value = 1, message = "Action quantity must be greater than zero")
        int actionQuantity,
        @NotNull(message = "broker is required")
        String brokerName
) {}
