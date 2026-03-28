package com.example.financialSystem.models.dto.responses;

import com.example.financialSystem.models.enums.FinancialType;
import com.example.financialSystem.models.enums.InvestmentType;
import com.example.financialSystem.utils.BenchMarkRate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class InvestmentResponse {
    private UUID id;
    private InvestmentType investmentType;
    private FinancialType financialType;
    private BigDecimal value;
    private LocalDate dateFinancial;
    private BenchMarkRate baseCurrency;
    private int actionQuantity;
    private BigDecimal currentValue;
    private String brokerName;
    private int daysInvested;
}
