package com.example.financialSystem.dto.requests;

import com.example.financialSystem.model.enums.CostType;
import com.example.financialSystem.util.BenchMarkRate;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CostRequest(
        @NotNull(message = "type is required")
        CostType costType,
        @NotNull(message = "observations is required")
        String observation,
        BigDecimal value,
        LocalDate dateFinancial,
        BenchMarkRate baseCurrency

) {
}
