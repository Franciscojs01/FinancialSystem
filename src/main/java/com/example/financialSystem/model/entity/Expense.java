package com.example.financialSystem.model.entity;

import com.example.financialSystem.model.enums.ExpenseType;
import com.example.financialSystem.model.enums.FinancialType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Expense extends Financial {
    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    private String paymentMethod;
    private boolean isFixed;

}
