package com.example.financialSystem.models.dto.requests;

import com.example.financialSystem.models.enums.CostType;
import com.example.financialSystem.utils.BenchMarkRate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CostRequest(
        @Schema(description = "Name of the cost type", example = "FIXED, VARIABLE, OPERATIONAL, TAX, OTHER")
        @NotNull(message = "type is required")
        CostType costType,

        @Schema(description = "observations about the cost", example = "Monthly subscription fee")
        @NotBlank(message = "observations is required")
        String observation,

        @Schema(description = "value of the cost", example = "100.00")
        @NotNull(message = "value is required")
        BigDecimal value,

        @Schema(description = "date of the financial registry", example = "2024-06-01")
        @NotNull(message = "dateFinancial is required")
        LocalDate dateFinancial,

        @Schema(description = "base currency for the cost", example = "USD, EUR, BRL")
        @NotNull(message = "baseCurrency is required")
        BenchMarkRate baseCurrency

) {
}
