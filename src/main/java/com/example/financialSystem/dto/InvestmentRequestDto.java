package com.example.financialSystem.dto;

import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.util.BenchMarkRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentRequestDto(
        InvestmentType type,
        BigDecimal value,
        BenchMarkRate baseCurrency,
        LocalDate dateFinancial,
        int actionQuantity,
        String brokerName
) {

}
