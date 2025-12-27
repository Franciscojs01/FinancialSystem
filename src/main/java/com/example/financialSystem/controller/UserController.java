package com.example.financialSystem.controller;

import com.example.financialSystem.model.dto.requests.UserAdminRequest;
import com.example.financialSystem.model.dto.requests.UserPatchRequest;
import com.example.financialSystem.model.dto.requests.UserRequest;
import com.example.financialSystem.model.dto.responses.UserResponse;
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

    @PostMapping("/admin/create")
    public ResponseEntity<UserResponse> registerAdminUser(@Valid @RequestBody UserAdminRequest request) {
        return ResponseEntity.ok().body(userService.registerAdminUser(request));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok().body(userService.registerUser(request));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<UserResponse> editUser(@PathVariable int id, @RequestBody UserRequest request) {
        return ResponseEntity.ok().body(userService.userUpdate(id, request));
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<UserResponse> patchUser(@PathVariable int id, @RequestBody UserPatchRequest request) {
        return ResponseEntity.ok().body(userService.userPatch(id, request));
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok().body(userService.listAllUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable int id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
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
