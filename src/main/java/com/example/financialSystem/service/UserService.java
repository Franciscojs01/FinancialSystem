package com.example.financialSystem.service;

import com.example.financialSystem.exception.UserDuplicateException;
import com.example.financialSystem.exception.UserNotFoundException;
import com.example.financialSystem.model.dto.requests.UserAdminRequest;
import com.example.financialSystem.model.dto.requests.UserRequest;
import com.example.financialSystem.model.dto.responses.UserResponse;
import com.example.financialSystem.model.entity.Login;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.model.enums.UserRole;
import com.example.financialSystem.repository.LoginRepository;
import com.example.financialSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse registerAdminUser(UserAdminRequest request) {
        loginRepository.findByUsername(request.getEmail())
                .ifPresent(existing -> {
                    throw new UserDuplicateException("email", request.getEmail());
                });

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setName(request.getName());
        newUser.setRegisterDate(LocalDate.now());
        newUser.setUserState(true);
        newUser.setUserRole(UserRole.ADMIN);


        Login login = new Login(
                newUser,
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        newUser.setLogin(login);

        userRepository.save(newUser);

        return new UserResponse(newUser.getName(), newUser.getEmail());
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

        newUser.setUserRole(UserRole.USER);

        Login login = new Login(
                newUser,
                email,
                passwordEncoder.encode(request.getPassword())
        );

        newUser.setLogin(login);

        userRepository.save(newUser);
        return new UserResponse(newUser.getName(), newUser.getEmail());
    }

    public UserResponse userUpdate(int id, UserRequest userDto) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id));

        validateOwnerShip(user);

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
            user.getLogin().setUsername(userDto.getEmail());
        }

        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.getLogin().setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        userRepository.save(user);

        return new UserResponse(userDto.getName(), userDto.getEmail());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> listUser() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getEmail(), user.getName()))
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!user.isUserState()) {
            throw new IllegalStateException("User is in inactive");
        }

        user.setUserState(false);
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void activateUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.isUserState()) {
            throw new IllegalStateException("User is already active");
        }

        user.setUserState(true);
        userRepository.save(user);
    }

    private void validateOwnerShip(User user) {
        Login loggedUser = getLoggedUser();

        boolean isOwnerOrAdmin = loggedUser.getUser().getId() == user.getId() ||
                loggedUser.getUser().getUserRole() == UserRole.ADMIN;

        if (!isOwnerOrAdmin) {
            throw new AccessDeniedException("You can only edit your own account");
        }
    }

}
