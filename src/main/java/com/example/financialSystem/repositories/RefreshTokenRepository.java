package com.example.financialSystem.repositories;

import com.example.financialSystem.models.entity.RefreshToken;
import com.example.financialSystem.models.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    @EntityGraph(attributePaths = {"user"})
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    @EntityGraph(attributePaths = {"user"})
    Optional<RefreshToken> findByUser_Id(UUID userId);
}
