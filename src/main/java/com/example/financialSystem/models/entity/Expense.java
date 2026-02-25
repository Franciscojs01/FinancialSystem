package com.example.financialSystem.models.entity;

import com.example.financialSystem.models.enums.ExpenseType;
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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Expense extends Financial {
    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    private String paymentMethod;
    private boolean isFixed;

}
