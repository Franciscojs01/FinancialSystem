package com.example.financialSystem.repository;

import com.example.financialSystem.model.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Integer> {
    @EntityGraph(attributePaths = {"login"})
    Optional<User> findById(int id);

    @EntityGraph(attributePaths = {"login"})
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    List<User> findAllActive();
}
