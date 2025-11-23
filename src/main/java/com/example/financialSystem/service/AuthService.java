package com.example.financialSystem.service;

import com.example.financialSystem.dto.requests.LoginRequest;
import com.example.financialSystem.dto.responses.LoginResponse;
import com.example.financialSystem.model.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

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
