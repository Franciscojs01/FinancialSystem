package com.example.financialSystem.model;

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

    public Double getRate() {
        return rate;
    }
}
