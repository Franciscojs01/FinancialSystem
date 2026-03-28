package com.example.financialSystem.utils;

import lombok.Getter;

@Getter
public enum BenchMarkRate {
    BRL("CDI"),
    USD("Fedaral funds Rate"),
    EUR("Euribor");

    private final String name;

    BenchMarkRate(String name) {
        this.name = name;
    }
}
