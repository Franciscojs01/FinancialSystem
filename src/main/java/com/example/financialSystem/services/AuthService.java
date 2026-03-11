package com.example.financialSystem.services;

import com.example.financialSystem.models.dto.requests.LoginRequest;
import com.example.financialSystem.models.dto.responses.LoginResponse;
import com.example.financialSystem.models.dto.responses.RefreshTokenResponse;
import com.example.financialSystem.models.entity.Login;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final PersistentTokenBasedRememberMeServices rememberMeServices;

    public AuthService(AuthenticationManager authenticationManager,
                       TokenService tokenService,
                       RefreshTokenService refreshTokenService,
                       PersistentTokenBasedRememberMeServices rememberMeServices) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.rememberMeServices = rememberMeServices;
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        Authentication tokenAuthenticate = new UsernamePasswordAuthenticationToken(
                loginRequest.username(), loginRequest.password()
        );
        Authentication authentication = authenticationManager.authenticate(tokenAuthenticate);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Login login = (Login) authentication.getPrincipal();

        String token = tokenService.generateToken(login);

        String refreshToken = null;
        if (!loginRequest.rememberMe()) {
            RefreshTokenResponse refreshTokenResponse = refreshTokenService
                    .createRefreshToken(login.getUser().getId(), false);
            refreshToken = refreshTokenResponse.refreshToken();
        } else {
            rememberMeServices.loginSuccess(request, response, authentication);
        }

        return new LoginResponse(login.getId(), login.getUsername(), token, refreshToken);
    }
}
