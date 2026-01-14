package com.example.financialSystem.model.listener;

import com.example.financialSystem.model.entity.Cost;
import com.example.financialSystem.model.entity.Expense;
import com.example.financialSystem.model.entity.Investment;
import com.example.financialSystem.model.enums.FinancialType;
import jakarta.persistence.PrePersist;

public class FinancialTypeListener {

    @PrePersist
    public void setFinancialType(Object entity) {
        if (entity instanceof Investment) {
            ((Investment) entity).setFinancialType(FinancialType.INVESTMENT);
        }

        if (entity instanceof Expense) {
            ((Expense) entity).setFinancialType(FinancialType.EXPENSE);
        }

        if (entity instanceof Cost) {
            ((Cost) entity).setFinancialType(FinancialType.COST);
        }
    }
}
