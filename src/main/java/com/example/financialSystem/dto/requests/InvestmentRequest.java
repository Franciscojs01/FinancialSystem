package com.example.financialSystem.dto.requests;

import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.util.BenchMarkRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentRequest(
        InvestmentType investmentType,
        BigDecimal value,
        BenchMarkRate baseCurrency,
        LocalDate dateFinancial,
        int actionQuantity,
        String brokerName
) {}
