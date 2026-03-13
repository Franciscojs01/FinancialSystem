package com.example.financialSystem.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "persistent_logins")
public class PersistentLogin {

    @Id
    @Column(name = "series", length = 64, nullable = false, unique = true)
    private String series;

    @Column(name = "username", length = 64, nullable = false)
    private String username;

    @Column(name = "token", length = 64, nullable = false)
    private String token;

    @Column(name = "last_used", nullable = false)
    private LocalDateTime lastUsed;
}

