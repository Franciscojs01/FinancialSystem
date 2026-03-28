package com.example.financialSystem.services;

import com.example.financialSystem.models.dto.responses.RefreshTokenResponse;
import com.example.financialSystem.models.entity.RefreshToken;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.mapper.RefreshTokenMapper;
import com.example.financialSystem.repositories.RefreshTokenRepository;
import com.example.financialSystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${app.jwtRefreshExpirationMs}")
    private Long jwtRefreshExpirationMs;

    private final RefreshTokenMapper refreshTokenMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenMapper refreshTokenMapper, RefreshTokenRepository tokenRepo, UserRepository userRepo) {
        this.refreshTokenMapper = refreshTokenMapper;
        this.refreshTokenRepository = tokenRepo;
        this.userRepository = userRepo;
    }

    public RefreshTokenResponse createRefreshToken(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtRefreshExpirationMs));

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        return refreshTokenMapper.toRefreshToken(saved);
    }

    public void verifyExpiration(RefreshToken token) {
        if (isTokenExpired(token)) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }


}
