package com.example.financialSystem.repositories;

import com.example.financialSystem.models.entity.Login;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface LoginRepository extends BaseRepository<Login, Integer> {
    @EntityGraph(attributePaths = {"user"})
     Optional<Login> findByUsername(String username);
}
