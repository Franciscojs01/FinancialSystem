package com.example.financialSystem.model.dto.requests;

import com.example.financialSystem.model.enums.CostType;
import com.example.financialSystem.util.BenchMarkRate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CostRequest(
        @NotNull(message = "type is required")
        CostType costType,

        @NotBlank(message = "observations is required")
        String observation,

        @NotNull(message = "value is required")
        BigDecimal value,

        @NotNull(message = "dateFinancial is required")
        LocalDate dateFinancial,

        @NotNull(message = "baseCurrency is required")
        BenchMarkRate baseCurrency

) {
}
