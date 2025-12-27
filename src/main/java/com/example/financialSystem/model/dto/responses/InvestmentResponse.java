package com.example.financialSystem.model.dto.responses;

import com.example.financialSystem.model.enums.FinancialType;
import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.util.BenchMarkRate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.example.financialSystem.model.enums.FinancialType.INVESTMENT;

@Getter
@Setter
@NoArgsConstructor
public class InvestmentResponse {
    private int id;
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
