package com.example.financialSystem.util.Cost;



import com.example.financialSystem.models.dto.requests.CostPatchRequest;
import com.example.financialSystem.models.dto.requests.CostRequest;
import com.example.financialSystem.models.enums.CostType;
import com.example.financialSystem.utils.BenchMarkRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CostRequestCreator {

    public static CostRequest createCostRequest() {
        return new CostRequest(
                CostType.FIXED,
                "Monthly subscription fee",
                new BigDecimal("100.00"),
                LocalDate.now().minusDays(1),
                BenchMarkRate.BRL
        );
    }

    public static CostRequest createUpdatedCostRequest() {
        return new CostRequest(
                CostType.VARIABLE,
                "Updated maintenance fee",
                new BigDecimal("250.00"),
                LocalDate.now().minusDays(2),
                BenchMarkRate.USD
        );
    }

    public static CostPatchRequest createCostPatchRequest() {
        return new CostPatchRequest(
                CostType.OPERATIONAL,
                "Partial update",
                new BigDecimal("150.00"),
                null,
                null
        );
    }
}
