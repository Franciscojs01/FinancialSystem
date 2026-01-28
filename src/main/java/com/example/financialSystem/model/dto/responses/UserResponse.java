package com.example.financialSystem.model.dto.responses;

import java.time.LocalDate;

import com.example.financialSystem.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private String name;
    private String email;
    private LocalDate anniversaryDate;
}
