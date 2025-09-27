package com.example.financialSystem.model;

public enum ExpensesType {
    FOOD("Food", "Expenses related to food and drinks"),
    TRANSPORT("Transport", "Costs for getting around"),
    HOUSING("Housing", "Home bills and rent"),
    HEALTH("Health", "Medical and pharmacy expenses"),
    LEISURE("Leisure", "Entertainment costs"),
    OTHER("Other", "Uncategorized expenses");

    private final String name;
    private final String details;

    ExpensesType(String name, String details) {
        this.name = name;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }
}
