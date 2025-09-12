package com.example.financialSystem.service;

import com.example.financialSystem.model.Login;
import com.example.financialSystem.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public abstract class UserLoggedService {
    @Autowired
    private LoginRepository loginRepository;

    protected Login getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return loginRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }
}
