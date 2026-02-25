package com.example.financialSystem.util.Cost;

import com.example.financialSystem.models.entity.Cost;
import com.example.financialSystem.models.enums.CostType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CostCreator {
        public static Cost createCost() {
            return Cost.builder()
                    .costType(CostType.FIXED)
                    .value(BigDecimal.valueOf(100.00))
                    .observation("Test Cost")
                    .dateFinancial(LocalDate.of(2010, 1, 2))
                    .build();
        }


}
