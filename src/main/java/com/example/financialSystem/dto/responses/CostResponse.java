package com.example.financialSystem.dto.responses;

import com.example.financialSystem.model.Cost;
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
public class CostResponse {
    private int idCost;
    private CostType costType;
    private String observation;
    private BigDecimal value;
    private BenchMarkRate baseCurrency;
    private LocalDate dateFinancial;

    public CostResponse(Cost entityCost) {
        this.idCost = entityCost.getId();
        this.costType = entityCost.getType();
        this.observation = entityCost.getObservation();
        this.value = entityCost.getValue();
        this.baseCurrency = entityCost.getBaseCurrency();
        this.dateFinancial = entityCost.getDateFinancial();
    }
}
