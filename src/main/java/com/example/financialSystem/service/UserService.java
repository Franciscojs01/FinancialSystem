package com.example.financialSystem.service;

import com.example.financialSystem.dto.UserDto;
import com.example.financialSystem.dto.UserRegisterDto;
import com.example.financialSystem.exceptions.UserDuplicateException;
import com.example.financialSystem.model.Login;
import com.example.financialSystem.model.User;
import com.example.financialSystem.repository.LoginRepository;
import com.example.financialSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loginRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDto registerUser(UserRegisterDto userRegisterDto) {
        String email = userRegisterDto.getEmail();

        loginRepository.findByUsername(email)
                .ifPresent(userRegistered -> {
                    throw new UserDuplicateException("User with: " + email + " already exists");
                });

        User newUser = new User(
                userRegisterDto.getName(),
                userRegisterDto.getEmail(),
                LocalDate.now(),
                true
        );

        String encondedPassword = passwordEncoder.encode(userRegisterDto.getPassword());

        Login login = new Login(newUser,email, encondedPassword);

        loginRepository.save(login);

        return new UserDto(newUser.getName(), newUser.getEmail());

    }

}
