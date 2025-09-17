package com.example.financialSystem.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Financial {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private BigDecimal value;
    private String baseCurrency;
    private LocalDate dateFinancial;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Financial(BigDecimal value, String baseCurrency, LocalDate dateFinancial, User user) {
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

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
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
