package com.example.financialSystem.util.Investment;

import com.example.financialSystem.models.dto.requests.InvestmentPatchRequest;
import com.example.financialSystem.models.dto.requests.InvestmentRequest;
import com.example.financialSystem.utils.BenchMarkRate;
import com.example.financialSystem.models.enums.InvestmentType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvestmentRequestCreator {

    public static InvestmentRequest createInvestmentRequest() {
        return new InvestmentRequest(
                InvestmentType.STOCK,
                new BigDecimal("5000.00"),
                LocalDate.now().minusDays(30),
                BenchMarkRate.BRL,
                1,
                "XP Investimentos"
        );
    }

    public static InvestmentRequest createUpdatedInvestmentRequest() {
        return new InvestmentRequest(
                InvestmentType.FUND,
                new BigDecimal("1000.00"),
                LocalDate.now().minusDays(60),
                BenchMarkRate.USD,
                2,
                "DF Investimentos"
        );
    }

    public static InvestmentPatchRequest createInvestmentPatchRequest() {
        return new InvestmentPatchRequest(
                InvestmentType.CRYPTO,
                new BigDecimal("7500.00"),
                null,
                null,
                null,
                null
        );
    }
}
