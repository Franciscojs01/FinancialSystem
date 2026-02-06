package com.example.financialSystem.service;

import com.example.financialSystem.model.dto.requests.LoginRequest;
import com.example.financialSystem.model.dto.responses.LoginResponse;
import com.example.financialSystem.model.entity.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication tokenAuthenticate = new UsernamePasswordAuthenticationToken(
                loginRequest.username(), loginRequest.password()
        );
        Authentication authentication = authenticationManager.authenticate(tokenAuthenticate);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Login login = (Login) authentication.getPrincipal();

        var token = tokenService.generateToken(login);

        return new LoginResponse(login.getId(), login.getUsername(), token);
    }
}
