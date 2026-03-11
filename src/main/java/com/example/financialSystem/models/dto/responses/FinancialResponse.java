package com.example.financialSystem.models.dto.responses;

import com.example.financialSystem.models.enums.FinancialType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FinancialResponse {
    private UUID id;
    private BigDecimal value;
    private LocalDate dateFinancial;
    private FinancialType financialType;
    private Boolean deleted;
}
