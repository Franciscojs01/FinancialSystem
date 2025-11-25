package com.example.financialSystem.controller;

import com.example.financialSystem.model.dto.responses.UserResponse;
import com.example.financialSystem.model.dto.requests.UserRequest;
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
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok().body(userService.registerUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> editUser(@PathVariable int id, @RequestBody UserRequest request) {
        return ResponseEntity.ok().body(userService.userUpdate(id, request));
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok().body(userService.listUser());
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable int id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}/active")
    public ResponseEntity<Void> activateUser(@PathVariable int id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }


}
