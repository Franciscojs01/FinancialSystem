package com.example.financialSystem.models.enums;

import lombok.Getter;

@Getter
public enum InvestmentType {
    STOCK(null),
    FUND(null),
    CRYPTO(null),
    FIXED_INCOME(0.08),
    TREASURY(0.10);

    private final Double rate;

    InvestmentType(Double rate) {
        this.rate = rate;
    }

}
