package com.example.financialSystem.repository;

import com.example.financialSystem.model.entity.Login;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.model.enums.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test for User Repository")
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

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

        savedUser.setDeleted(true);
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

    private User createUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@gmail.com");
        user.setAnniversaryDate(LocalDate.of(2008, 1, 1));
        user.setUserRole(UserRole.USER);
        user.setDeleted(false);

        Login login = new Login();
        login.setUsername(user.getEmail());
        login.setPassword("123");
        login.setUser(user);

        user.setLogin(login);

        return user;
    }

    private User createUserAdmin() {
        User user = new User();
        user.setName("adminn");
        user.setEmail("admin@.com");
        user.setAnniversaryDate(LocalDate.of(2008, 1, 1));
        user.setUserRole(UserRole.ADMIN);
        user.setDeleted(false);

        Login login = new Login();
        login.setUsername(user.getEmail());
        login.setPassword("admin123");
        login.setUser(user);

        user.setLogin(login);

        return user;
    }

}