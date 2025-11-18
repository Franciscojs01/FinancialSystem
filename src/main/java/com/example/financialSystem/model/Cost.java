package com.example.financialSystem.model;

import com.example.financialSystem.model.enums.CostType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cost extends Financial {
    private String observation;

    @Enumerated(EnumType.STRING)
    private CostType type;

}
