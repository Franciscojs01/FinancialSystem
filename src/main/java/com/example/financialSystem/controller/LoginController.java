package com.example.financialSystem.controller;

import com.example.financialSystem.dto.LoginDataDto;
import com.example.financialSystem.dto.LoginDto;
import com.example.financialSystem.model.Login;
import com.example.financialSystem.model.User;
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
    public ResponseEntity<LoginDto> login(@RequestBody LoginDataDto loginDataDto) {
        Authentication userTokenAuthenticate = new UsernamePasswordAuthenticationToken(loginDataDto.username(), loginDataDto.password());
        Authentication authentication = authenticationManager.authenticate(userTokenAuthenticate);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Login login = (Login) authentication.getPrincipal();

        var token = tokenService.generateToken(login);

        return ResponseEntity.ok(new LoginDto(login.getId(), login.getUsername(), token));
    }
}
