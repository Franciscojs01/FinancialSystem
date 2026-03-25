package com.example.financialSystem.services;

import com.example.financialSystem.models.dto.requests.LoginRequest;
import com.example.financialSystem.models.dto.responses.LoginResponse;
import com.example.financialSystem.models.dto.responses.RefreshTokenResponse;
import com.example.financialSystem.models.entity.Login;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;


    public AuthService(AuthenticationManager authenticationManager,
                       TokenService tokenService,
                       RefreshTokenService refreshTokenService
                       ) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication tokenAuthenticate = new UsernamePasswordAuthenticationToken(
                loginRequest.username(), loginRequest.password()
        );
        Authentication authentication = authenticationManager.authenticate(tokenAuthenticate);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Login login = (Login) authentication.getPrincipal();

        String token = tokenService.generateToken(login);

        RefreshTokenResponse refreshTokenResponse = refreshTokenService
                .createRefreshToken(login.getUser().getId());

        return new LoginResponse(login.getId(), login.getUsername(), token, refreshTokenResponse.refreshToken());
    }
}
