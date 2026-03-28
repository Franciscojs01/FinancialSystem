package com.example.financialSystem.models.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserFinancialResponse {
    private UUID id;
    private String name;
    private List<FinancialResponse> financials;
}
