package com.example.financialSystem.model.entity;


import com.example.financialSystem.model.enums.FinancialType;
import com.example.financialSystem.util.BenchMarkRate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Financial {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "financial_id")
    private int id;

    @NotNull(message = "Value is required")
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
