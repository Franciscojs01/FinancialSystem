package com.example.financialSystem.model.enums;

import lombok.Getter;

@Getter
public enum ExpenseType {
    FOOD("Food" ),
    TRANSPORT("Transport"),
    HOUSING("Housing"),
    HEALTH("Health"),
    LEISURE("Leisure"),
    OTHER("Other");

    private final String name;

    ExpenseType(String name) {
        this.name = name;
    }
}
