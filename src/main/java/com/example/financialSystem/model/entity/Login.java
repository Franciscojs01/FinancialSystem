package com.example.financialSystem.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Login extends BaseEntity implements UserDetails {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String username;
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user == null || user.getUserRole() == null) {
            return List.of();
        }

        return List.of(
                new SimpleGrantedAuthority(user.getUserRole().getRoleName())
        );
    }

    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isEnabled() {
        return user != null && Boolean.FALSE.equals(user.getDeleted());
    }

}

