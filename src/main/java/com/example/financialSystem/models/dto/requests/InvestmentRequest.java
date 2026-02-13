package com.example.financialSystem.models.dto.requests;

import com.example.financialSystem.models.enums.InvestmentType;
import com.example.financialSystem.utils.BenchMarkRate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentRequest(
        @Schema(description = "Name of the investment type", example = "STOCK, CRYPTO, TREASURY, FIXED_INCOME, FUND")
        @NotNull(message = "type is required")
        InvestmentType investmentType,

        @Schema(description = "value of the investment", example = "1000.00")
        @NotNull(message = "value is required")
        BigDecimal value,

        @Schema(description = "date of the financial registry", example = "2024-06-01")
        @NotNull(message = "dateFinancial is required")
        LocalDate dateFinancial,

        @Schema(description = "base currency for the investment", example = "USD, EUR, BRL")
        @NotNull(message = "baseCurrency is required")
        BenchMarkRate baseCurrency,

        @Schema(description = "quantity of actions for the investment", example = "10")
        @Min(value = 1, message = "Action quantity must be greater than zero")
        int actionQuantity,

        @Schema(description = "name of the broker", example = "Broker XYZ")
        @NotBlank(message = "broker is required")
        String brokerName
) {}
