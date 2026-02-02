package com.example.financialSystem.model.dto.responses;

import java.time.LocalDate;

import com.example.financialSystem.model.entity.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String name;
    private String email;
}
