package com.example.financialSystem.service;

import com.example.financialSystem.dto.UserDto;
import com.example.financialSystem.dto.UserResponseDto;
import com.example.financialSystem.exceptions.UserDuplicateException;
import com.example.financialSystem.model.Login;
import com.example.financialSystem.model.User;
import com.example.financialSystem.repository.LoginRepository;
import com.example.financialSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService extends UserLoggedService implements UserDetailsService {
    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Login login = loginRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!login.getUser().isUserState()) {
            throw new UsernameNotFoundException("User is inactive");
        }

        return login;
    }

    public UserDto registerUser(UserResponseDto userRegisterDto) {
        String email = userRegisterDto.getEmail();

        loginRepository.findByUsername(email)
                .ifPresent(userRegistered -> {
                    throw new UserDuplicateException("User with: " + email + " already exists");
                });

        User newUser = new User(
                userRegisterDto.getName(),
                email,
                LocalDate.now(),
                true
        );

        String encondedPassword = passwordEncoder.encode(userRegisterDto.getPassword());

        Login login = new Login(newUser, email, encondedPassword);
        newUser.setLogin(login);

        userRepository.save(newUser);

        return new UserDto(newUser.getName(), newUser.getEmail());

    }

    public UserDto editUser(int id, UserResponseDto userDto) {
        Login authenticatedLogin = getLoggedUser();
        if (authenticatedLogin.getUser().getId() != id) {
            throw new AccessDeniedException("You can only edit your own account");
        }

        User userExistent = userRepository.findById(id)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            userExistent.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            userExistent.setEmail(userDto.getEmail());
            userExistent.getLogin().setUsername(userDto.getEmail());
        }

        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            userExistent.getLogin().setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        userRepository.save(userExistent);

        return new UserDto(userDto.getName(), userDto.getEmail());
    }

    public List<UserDto> listUser() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getEmail(), user.getName()))
                .toList();
    }

    public void deactivateUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isUserState()) {
            throw new IllegalStateException("User is in inactive");
        }

        user.setUserState(false);
        userRepository.save(user);
    }

    public void activateUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isUserState()) {
            throw new IllegalStateException("User is already active");
        }

        user.setUserState(true);
        userRepository.save(user);
    }

}
