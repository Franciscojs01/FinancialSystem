package com.example.financialSystem.repository;

import com.example.financialSystem.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Long> {
    public Login findByUsername(String username);
}
