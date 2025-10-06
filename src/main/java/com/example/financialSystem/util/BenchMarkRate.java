package com.example.financialSystem.util;

public enum BenchMarkRate {
    BRL("CDI", 0.1365),
    USD("Fedaral funds Rate", 0.0525),
    EUR("Euribor",0.04);

    private final String name;
    private final double annualRate;

    BenchMarkRate(String name, double annualRate) {
        this.name = name;
        this.annualRate = annualRate;
    }

    public String getName() {
        return name;
    }

    public double getAnnualRate() {
        return annualRate;
    }
}
