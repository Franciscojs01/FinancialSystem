package com.example.financialSystem.controller;

import com.example.financialSystem.dto.requests.LoginRequest;
import com.example.financialSystem.dto.responses.LoginResponse;
import com.example.financialSystem.model.Login;
import com.example.financialSystem.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication userTokenAuthenticate = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(userTokenAuthenticate);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Login login = (Login) authentication.getPrincipal();

        var token = tokenService.generateToken(login);

        return ResponseEntity.ok(new LoginResponse(login.getId(), login.getUsername(), token));
    }
}
