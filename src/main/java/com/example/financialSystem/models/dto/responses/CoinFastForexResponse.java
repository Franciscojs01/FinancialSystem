package com.example.financialSystem.models.dto.responses;


import lombok.Data;

import java.util.Map;

@Data
public class CoinFastForexResponse {
    private String base;
    private Map<String, Double> results;
    private String updated;
}
