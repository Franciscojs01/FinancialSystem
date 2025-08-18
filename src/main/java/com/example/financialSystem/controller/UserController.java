package com.example.financialSystem.controller;

import com.example.financialSystem.dto.UserDto;
import com.example.financialSystem.dto.UserEditDto;
import com.example.financialSystem.dto.UserRegisterDto;
import com.example.financialSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserRegisterDto userRegisterDto) {
        UserDto userDto = userService.registerUser(userRegisterDto);
        return ResponseEntity.ok().body(userDto);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<UserDto> updateUser(@PathVariable int id, @RequestBody UserEditDto userEditDto) {
//        UserDto userEdited = userService.editUser(id, userEditDto);
//        return ResponseEntity.ok().body(userEdited);
//    }

}
