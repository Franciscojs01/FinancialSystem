package com.example.financialSystem.models.dto.responses;

import com.example.financialSystem.models.enums.CostType;
import com.example.financialSystem.models.enums.FinancialType;
import com.example.financialSystem.utils.BenchMarkRate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CostResponse {
    private int id;
    private CostType costType;
    private FinancialType financialType;
    private String observation;
    private BigDecimal value;
    private BenchMarkRate baseCurrency;
    private LocalDate dateFinancial;

}
