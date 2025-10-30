package com.example.financialSystem.controller;

import com.example.financialSystem.dto.UserResponse;
import com.example.financialSystem.dto.UserRequest;
import com.example.financialSystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest userData) {
        UserResponse userDto = userService.registerUser(userData);
        return ResponseEntity.ok().body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable int id, @RequestBody UserRequest userEditDto) {
        UserResponse userEdited = userService.editUser(id, userEditDto);
        return ResponseEntity.ok().body(userEdited);
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.listUser();

        return ResponseEntity.ok().body(users);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable int id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{id}/active")
    public ResponseEntity<Void> activateUser(@PathVariable int id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }


}
