package com.example.financialSystem.services;

import com.example.financialSystem.models.dto.responses.RefreshTokenResponse;
import com.example.financialSystem.models.entity.RefreshToken;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.mapper.RefreshTokenMapper;
import com.example.financialSystem.repositories.RefreshTokenRepository;
import com.example.financialSystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${app.jwtRefreshExpirationMs}")
    private Long jwtRefreshExpirationMs;

    @Value("${app.jwtRefreshRememberMeExpirationMs}")
    private Long jwtRefreshRememberMeExpirationMs;

    private final RefreshTokenMapper refreshTokenMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenMapper refreshTokenMapper, RefreshTokenRepository tokenRepo, UserRepository userRepo) {
        this.refreshTokenMapper = refreshTokenMapper;
        this.refreshTokenRepository = tokenRepo;
        this.userRepository = userRepo;
    }

    public RefreshTokenResponse createRefreshToken(UUID userId, boolean rememberMe) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.findByUser_Id(userId)
                .ifPresent(refreshTokenRepository::delete);

        long expiration = rememberMe ? jwtRefreshRememberMeExpirationMs : jwtRefreshExpirationMs;

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(expiration));
        refreshToken.setRememberMe(rememberMe);

        RefreshToken saved = refreshTokenRepository.save(refreshToken);

        return refreshTokenMapper.toRefreshToken(saved);
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.isRememberMe()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("This user uses Remember Me cookie. Use the cookie-based flow instead.");
        }
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

    @Transactional
    public void deleteByUserId(UUID userId) {
        userRepository.findById(userId).ifPresent(refreshTokenRepository::deleteByUser);
    }


}
