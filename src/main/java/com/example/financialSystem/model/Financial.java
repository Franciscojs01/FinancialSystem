package com.example.financialSystem.model;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Financial {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String type;
    private BigDecimal value;
    private LocalDate dateFinancial;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Financial(String type, BigDecimal value, LocalDate dateFinancial, User user) {
        this.type = type;
        this.value = value;
        this.dateFinancial = dateFinancial;
        this.user = user;
    }

    public Financial() {

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setDateFinancial(LocalDate dateFinancial) {
        this.dateFinancial = dateFinancial;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
