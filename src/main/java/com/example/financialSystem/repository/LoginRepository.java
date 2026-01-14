package com.example.financialSystem.repository;

import com.example.financialSystem.model.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends BaseRepository<Login, Integer> {
     Optional<Login> findByUsername(String username);
}
