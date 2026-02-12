package com.example.financialSystem.models.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserFinancialResponse {
    private int id;
    private String name;
    private List<FinancialResponse> financials;
}
