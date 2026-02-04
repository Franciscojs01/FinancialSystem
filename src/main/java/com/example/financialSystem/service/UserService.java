package com.example.financialSystem.service;

import com.example.financialSystem.exception.duplicate.UserDuplicateException;
import com.example.financialSystem.exception.notFound.UserNotFoundException;
import com.example.financialSystem.model.dto.requests.UserAdminRequest;
import com.example.financialSystem.model.dto.requests.UserPatchRequest;
import com.example.financialSystem.model.dto.requests.UserRequest;
import com.example.financialSystem.model.dto.responses.UserResponse;
import com.example.financialSystem.model.entity.Login;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.model.enums.UserRole;
import com.example.financialSystem.model.mapper.UserMapper;
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

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Login login = loginRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username)
                );

        if (login.getUser() == null || Boolean.TRUE.equals(login.getUser().getDeleted())) {
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

        User newUser = userMapper.toEntityAdmin(request);
        if (request.getAnniversaryDate() != null) {
            newUser.setAnniversaryDate(request.getAnniversaryDate());
        }
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
        loginRepository.findByUsername(request.getEmail())
                .ifPresent(existing -> {
                    throw new UserDuplicateException("email", request.getEmail());
                });

        User newUser = userMapper.toEntity(request);
        if (request.getAnniversaryDate() != null) {
            newUser.setAnniversaryDate(request.getAnniversaryDate());
        }
        newUser.setUserRole(UserRole.USER);

        Login login = new Login(
                newUser,
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        newUser.setLogin(login);

        userRepository.save(newUser);
        return new UserResponse(newUser.getName(), newUser.getEmail());
    }

    public UserResponse userUpdate(int id, UserRequest request) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id));

        validateOwnerShip(user);
        ensureChanged(user, request);

        userMapper.updateEntityFromUpdate(request, user);

        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse userPatch(int id, UserPatchRequest patchRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        validateOwnerShip(existingUser);

        userMapper.updateEntityFromPatch(patchRequest, existingUser);

        return userMapper.toResponse(userRepository.save(existingUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        validateOwnerShip(user);

        return userMapper.toResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> listAllUser() {
        return userMapper.toResponseList(userRepository.findAllActive());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.getDeleted()) {
            throw new IllegalStateException("User is already inactive");
        }

        user.setDeleted(true);
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void activateUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!user.getDeleted()) {
            throw new IllegalStateException("User is already active");
        }

        user.setDeleted(false);
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

    private void ensureChanged(User oldUser, UserRequest newUserReq) {
        UserRequest oldAsRequest = userMapper.toRequest(oldUser);
        if (oldAsRequest.equals(newUserReq)) throw new IllegalArgumentException("No change in this user");
    }
}
