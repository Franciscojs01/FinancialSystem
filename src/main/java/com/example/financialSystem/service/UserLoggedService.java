package com.example.financialSystem.service;

import com.example.financialSystem.model.entity.Login;
import com.example.financialSystem.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public abstract class UserLoggedService {
    @Autowired
    private LoginRepository loginRepository;

    protected Login getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof Login) {
            return (Login) auth.getPrincipal();
        }

        return loginRepository.findByUsername(auth != null ? auth.getName() : null)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
    }
}
