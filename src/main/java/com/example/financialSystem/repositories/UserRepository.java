package com.example.financialSystem.repositories;

import com.example.financialSystem.models.entity.User;
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

    @EntityGraph(attributePaths = {"financial","login"})
    @Query("select u from User u where u.deleted = false")
    List<User> findAllWithFinancials();
}
