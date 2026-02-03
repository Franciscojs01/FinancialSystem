package com.example.financialSystem.repository;

import com.example.financialSystem.model.entity.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Integer> {
    Optional<User> findById(int id);


    Object existsByEmail(String s);
}
