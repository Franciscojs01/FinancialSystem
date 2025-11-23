package com.example.financialSystem.model;

import com.example.financialSystem.model.enums.ExpenseType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Expense extends Financial {
    @Enumerated(EnumType.STRING)
    private ExpenseType type;

    private String paymentMethod;
    private boolean isFixed;

}
