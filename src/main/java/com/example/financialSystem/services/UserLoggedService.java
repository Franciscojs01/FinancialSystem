package com.example.financialSystem.services;

import com.example.financialSystem.models.entity.Login;
import com.example.financialSystem.repositories.LoginRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public abstract class UserLoggedService {
    private final LoginRepository loginRepository;

    protected UserLoggedService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    protected Login getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof Login) {
            return (Login) auth.getPrincipal();
        }

        return loginRepository.findByUsername(auth != null ? auth.getName() : null)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
    }
}
