package com.example.taskmanagement.controller;

import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.jwt.JWTUtils;
import com.example.taskmanagement.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final JWTUtils jwtUtils;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        return ResponseEntity.ok("User registered: " + registeredUser.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        User user = userService.authenticate(email, password);
        String token = jwtUtils.generateToken(user);
        return ResponseEntity.ok(token);
    }

}
