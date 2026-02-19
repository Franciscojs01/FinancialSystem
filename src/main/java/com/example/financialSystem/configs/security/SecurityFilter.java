package com.example.financialSystem.configs.security;

import com.example.financialSystem.repositories.LoginRepository;
import com.example.financialSystem.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final LoginRepository loginRepository;
    private final TokenService tokenService;

    public SecurityFilter(LoginRepository loginRepository, TokenService tokenService) {
        this.loginRepository = loginRepository;
        this.tokenService = tokenService;
    }

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/user/register",
            "/user/admin/create"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest servletHttp, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isPublicPath(servletHttp)) {
            filterChain.doFilter(servletHttp, response);
            return;
        }

        String token = extractToken(servletHttp);
        if (token != null) {
            authenticateUser(token);
        }

        filterChain.doFilter(servletHttp, response);
    }

    private boolean isPublicPath(HttpServletRequest servletHttp) {
        String uri = servletHttp.getRequestURI();
        return PUBLIC_PATHS.stream().anyMatch(uri::startsWith);
    }

    private String extractToken(HttpServletRequest servletHttp) {
        String authHeader = servletHttp.getHeader("Authorization");
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
