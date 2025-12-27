package com.example.financialSystem.model.entity;

import com.example.financialSystem.model.enums.CostType;
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
@SuperBuilder
@Entity
public class Cost extends Financial {
    @Enumerated(EnumType.STRING)
    private CostType costType;

    private String observation;

}
