package com.example.financialSystem.security;

import com.example.financialSystem.repository.LoginRepository;
import com.example.financialSystem.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private TokenService tokenService;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/user/register"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isPublicPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);
        if (token != null) {
            authenticateUser(token);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(HttpServletRequest request) {
        return PUBLIC_PATHS.contains(request.getRequestURI());
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private void authenticateUser(String token) {
        String email = tokenService.validateToken(token);
        if (email != null) {
            loginRepository.findByUsername(email).ifPresent(usuario -> {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }


    }

}
