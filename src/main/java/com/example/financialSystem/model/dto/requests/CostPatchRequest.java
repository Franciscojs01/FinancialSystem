package com.example.financialSystem.model.dto.requests;

import com.example.financialSystem.model.enums.CostType;
import com.example.financialSystem.util.BenchMarkRate;
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
    private CostType costType;
    private String observation;
    private BigDecimal value;
    private BenchMarkRate baseCurrency;
    private LocalDate dateFinancial;
}
