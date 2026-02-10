package com.example.financialSystem.model.dto.responses;

import com.example.financialSystem.model.enums.FinancialType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class FinancialResponse {
    private int id;
    private BigDecimal value;
    private LocalDate dateFinancial;
    private FinancialType financialType;
    private Boolean deleted;
}
