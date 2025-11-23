package com.example.financialSystem.service;

import com.example.financialSystem.dto.responses.UserResponse;
import com.example.financialSystem.dto.requests.UserRequest;
import com.example.financialSystem.exception.UserDuplicateException;
import com.example.financialSystem.exception.UserNotFoundException;
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
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        Login login = loginRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!login.getUser().isUserState()) {
            throw new UsernameNotFoundException("User is inactive");
        }

        return login;
    }

    public UserResponse registerUser(UserRequest request) {
        String email = request.getEmail();

        loginRepository.findByUsername(email)
                .ifPresent(existing -> {
                    throw new UserDuplicateException("email", email);
                });

        User newUser = new User(
                request.getName(),
                email,
                LocalDate.now(),
                true
        );

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Login login = new Login(newUser, email, encodedPassword);
        newUser.setLogin(login);

        userRepository.save(newUser);
        return new UserResponse(newUser.getName(), newUser.getEmail());
    }


    public UserResponse userUpdate(int id, UserRequest userDto) {
        Login authenticatedLogin = getLoggedUser();
        if (authenticatedLogin.getUser().getId() != id) {
            throw new AccessDeniedException("You can only edit your own account");
        }

        User userExistent = userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id));

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

        return new UserResponse(userDto.getName(), userDto.getEmail());
    }

    public List<UserResponse> listUser() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getEmail(), user.getName()))
                .toList();
    }

    public void deactivateUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!user.isUserState()) {
            throw new IllegalStateException("User is in inactive");
        }

        user.setUserState(false);
        userRepository.save(user);
    }

    public void activateUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.isUserState()) {
            throw new IllegalStateException("User is already active");
        }

        user.setUserState(true);
        userRepository.save(user);
    }

}
