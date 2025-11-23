package com.example.financialSystem.dto.requests;

import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.util.BenchMarkRate;
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
    private InvestmentType investmentType;
    private BigDecimal value;
    private BenchMarkRate baseCurrency;
    private LocalDate dateFinancial;
    private Integer actionQuantity;
    String brokerName;

}
