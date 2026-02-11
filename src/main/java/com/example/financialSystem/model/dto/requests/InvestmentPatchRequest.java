package com.example.financialSystem.model.dto.requests;

import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.util.BenchMarkRate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentPatchRequest {
    @Schema(description = "Name of the investment type", example = "STOCK, CRYPTO, TREASURY, FIXED_INCOME, FUND")
    private InvestmentType investmentType;
    @Schema(description = "value of the investment", example = "1000.00")
    private BigDecimal value;
    @Schema(description = "base currency for the investment", example = "USD, EUR, BRL")
    private BenchMarkRate baseCurrency;
    @Schema(description = "date of the financial registry", example = "2024-06-01")
    private LocalDate dateFinancial;
    @Schema(description = "quantity of actions for the investment", example = "10")
    @Min(value = 1, message = "Action quantity must be greater than zero")
    private Integer actionQuantity;
    @Schema(description = "name of the broker", example = "Broker XYZ")
    private String brokerName;

}
