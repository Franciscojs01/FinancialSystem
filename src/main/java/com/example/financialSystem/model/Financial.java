package com.example.financialSystem.model;


import com.example.financialSystem.util.BenchMarkRate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    public Financial(BigDecimal value, BenchMarkRate baseCurrency, LocalDate dateFinancial, User user) {
        this.value = value;
        this.baseCurrency = baseCurrency;
        this.dateFinancial = dateFinancial;
        this.user = user;
    }

    public Financial() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BenchMarkRate getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(BenchMarkRate baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public LocalDate getDateFinancial() {
        return dateFinancial;
    }

    public void setDateFinancial(LocalDate dateFinancial) {
        this.dateFinancial = dateFinancial;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
