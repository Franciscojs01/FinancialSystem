package com.example.financialSystem.models.entity;

import com.example.financialSystem.models.enums.CostType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cost extends Financial {
    @Enumerated(EnumType.STRING)
    private CostType costType;

    private String observation;

}
