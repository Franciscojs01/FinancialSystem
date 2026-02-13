package com.example.financialSystem.models.dto.requests;

import com.example.financialSystem.models.enums.CostType;
import com.example.financialSystem.utils.BenchMarkRate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CostPatchRequest {
    @Schema(description = "Name of the cost type", example = "FIXED, VARIABLE, OPERATIONAL, TAX, OTHER")
    private CostType costType;
    @Schema(description = "observations about the cost", example = "Monthly subscription fee")
    private String observation;
    @Schema(description = "value of the cost", example = "100.00")
    private BigDecimal value;
    @Schema(description = "base currency for the cost", example = "USD, EUR, BRL")
    private BenchMarkRate baseCurrency;
    @Schema(description = "date of the financial registry", example = "2024-06-01")
    private LocalDate dateFinancial;
}
