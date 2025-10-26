package com.example.financialSystem.controller;

import com.example.financialSystem.dto.UserDto;
import com.example.financialSystem.dto.UserEditDto;
import com.example.financialSystem.dto.UserRegisterDto;
import com.example.financialSystem.model.User;
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
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        UserDto userDto = userService.registerUser(userRegisterDto);
        return ResponseEntity.ok().body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable int id, @RequestBody UserEditDto userEditDto) {
        UserDto userEdited = userService.editUser(id, userEditDto);
        return ResponseEntity.ok().body(userEdited);
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.listUser();

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
