package com.example.financialSystem.util;

import com.example.financialSystem.model.dto.responses.UserResponse;
import com.example.financialSystem.model.entity.Login;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.model.enums.UserRole;

import java.time.LocalDate;

public class UserCreator {

    public static User createUser() {
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

    public static User createUserAdmin() {
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

    public static User createActiveUser() {
        User user = new User();
        user.setName("John Active");
        user.setEmail("john.active@gmail.com");
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

    public static User createInactiveUser() {
        User user = new User();
        user.setName("John Inactive");
        user.setEmail("john.inactive@gmail.com");
        user.setAnniversaryDate(LocalDate.of(2008, 1, 1));
        user.setUserRole(UserRole.USER);
        user.setDeleted(true);

        Login login = new Login();
        login.setUsername(user.getEmail());
        login.setPassword("123");
        login.setUser(user);

        user.setLogin(login);

        return user;
    }

    public static User updateUser() {
        User user = createUser();
        user.setName("Jane Doe");
        user.setEmail("jose@gmail.com");
        if (user.getLogin() != null) user.getLogin().setUsername(user.getEmail());
        return user;
    }

    public static User patchUser() {
        User user = createUser();
        user.setId(5);
        user.setName("John Smith");

        return user;
    }

    public static UserResponse createValidUser() {
        User user = createUser();
        return new UserResponse(user.getName(), user.getEmail());
    }

    public static UserResponse createValidAdminUser() {
        User user = createUserAdmin();
        return new UserResponse(user.getName(), user.getEmail());
    }

}