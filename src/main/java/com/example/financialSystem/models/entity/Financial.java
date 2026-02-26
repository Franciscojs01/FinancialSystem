package com.example.financialSystem.models.entity;


import com.example.financialSystem.models.enums.FinancialType;
import com.example.financialSystem.models.listener.FinancialTypeListener;
import com.example.financialSystem.utils.BenchMarkRate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(FinancialTypeListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Financial extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "financial_id")
    private int id;

    @DecimalMin(value = "0.01", message = "Value must be greater than 0")
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    private BenchMarkRate baseCurrency;

    @NotNull(message = "Date is required")
    private LocalDate dateFinancial;

    @Enumerated(EnumType.STRING)
    private FinancialType financialType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

}
