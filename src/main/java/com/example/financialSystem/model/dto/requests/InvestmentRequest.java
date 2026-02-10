package com.example.financialSystem.model.dto.requests;

import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.util.BenchMarkRate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentRequest(
        @NotNull(message = "type is required")
        InvestmentType investmentType,

        @NotNull(message = "value is required")
        BigDecimal value,

        @NotNull(message = "dateFinancial is required")
        LocalDate dateFinancial,

        @NotNull(message = "baseCurrency is required")
        BenchMarkRate baseCurrency,

        @Min(value = 1, message = "Action quantity must be greater than zero")
        int actionQuantity,
        @NotBlank(message = "broker is required")
        String brokerName
) {}
