package com.example.financialSystem.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String email;
    private LocalDate registerDate;
    private boolean userState;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Financial> financial;

    public User(String name, String email, LocalDate registerDate, boolean userState) {
        this.name = name;
        this.email = email;
        this.registerDate = registerDate;
        this.userState = userState;
    }

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public boolean isUserState() {
        return userState;
    }

    public void setUserState(boolean userState) {
        this.userState = userState;
    }

    public List<Financial> getFinancial() {
        return financial;
    }

    public void setFinancial(List<Financial> financial) {
        this.financial = financial;
    }
}
