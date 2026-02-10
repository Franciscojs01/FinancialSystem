package com.example.financialSystem.repository;

import com.example.financialSystem.model.entity.Login;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.util.UserCreator;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;


@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test for User Repository")
class UserRepositoryTest extends UserCreator {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Test
    @DisplayName("Save User When Successful")
    void save_PersistUser_WhenSuccessful() {
        User userToBeSaved = createUser();
        User savedUser = userRepository.save(userToBeSaved);
        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isNotNull();
        Assertions.assertThat(savedUser.getEmail()).isEqualTo(userToBeSaved.getEmail());
        Assertions.assertThat(savedUser.getLogin()).isNotNull();
        Assertions.assertThat(savedUser.getLogin().getUsername()).isEqualTo(userToBeSaved.getEmail());

    }

    @Test
    @DisplayName("Update User When Successful")
    void update_UpdateUser_WhenSuccessful() {
        User userToBeSaved = createUser();
        User savedUser = userRepository.save(userToBeSaved);

        savedUser.setName("Jane Doe");
        User updatedUser = userRepository.save(savedUser);

        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getId()).isNotNull();
        Assertions.assertThat(updatedUser.getName()).isEqualTo("Jane Doe");
    }

    @Test
    @DisplayName("Delete User When Successful")
    void delete_RemoveUser_WhenSuccessful() {
        User userToBeSaved = createUserAdmin();
        User savedUser = userRepository.saveAndFlush(userToBeSaved);

        savedUser.setDeleted(Boolean.TRUE);
        userRepository.saveAndFlush(savedUser);

        Optional<User> optionalUser = userRepository.findById(savedUser.getId());

        Assertions.assertThat(optionalUser).isNotEmpty();
        Assertions.assertThat(optionalUser.get().getDeleted()).isTrue();
    }

    @Test
    @DisplayName("Find All Active Users When Successful")
    void findAllActiveUsers_ReturnsListOfUsers_WhenSuccessful() {
        User userToBeSaved = createUserAdmin();
        User savedUser = userRepository.save(userToBeSaved);

        List<User> users = userRepository.findAllActive();

        Assertions.assertThat(users).isNotEmpty();
        Assertions.assertThat(users).contains(savedUser);
    }

    @Test
    @DisplayName("findByUsername returns login when successful")
    void findByUsername_ReturnsUser_WhenSuccessful() {
        User user = createUser();

        userRepository.saveAndFlush(user);
        entityManager.clear();
        String username = user.getEmail();

        Optional<Login> optionalLogin = loginRepository.findByUsername(username);

        Assertions.assertThat(optionalLogin).isPresent();
        Assertions.assertThat(optionalLogin.get().getUsername()).isEqualTo(username);
        Assertions.assertThat(optionalLogin.get().getUser()).isNotNull();
        Assertions.assertThat(optionalLogin.get().getUser().getEmail()).isEqualTo(user.getEmail());
    }

}