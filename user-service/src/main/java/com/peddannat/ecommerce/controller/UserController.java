package com.peddannat.ecommerce.controller;

import com.peddannat.ecommerce.dto.request.LoginRequest;
import com.peddannat.ecommerce.dto.request.RegisterRequest;
import com.peddannat.ecommerce.dto.response.LoginResponse;
import com.peddannat.ecommerce.dto.response.UserResponse;
import com.peddannat.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Public endpoint used to create a new user account.
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(request));
    }


    // Public endpoint used to authenticate a user and return a JWT token.
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }

    // Protected endpoint used to fetch user details by user id.
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
