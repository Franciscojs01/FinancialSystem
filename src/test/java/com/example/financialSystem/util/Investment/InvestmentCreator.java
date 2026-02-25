package com.example.financialSystem.util.Investment;

import com.example.financialSystem.models.entity.Investment;
import com.example.financialSystem.models.enums.InvestmentType;
import com.example.financialSystem.utils.BenchMarkRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvestmentCreator {

    public static Investment createInvestment() {
        return Investment.builder()
                .investmentType(InvestmentType.STOCK)
                .value(BigDecimal.valueOf(1000.00))
                .baseCurrency(BenchMarkRate.BRL)
                .dateFinancial(LocalDate.of(2024, 6, 1))
                .actionQuantity(10)
                .brokerName("Broker XYZ")
                .deleted(false)
                .build();
    }

}
