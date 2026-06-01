package com.peddannat.ecommerce.controller;

import com.peddannat.ecommerce.dto.request.LoginRequest;
import com.peddannat.ecommerce.dto.request.RegisterRequest;
import com.peddannat.ecommerce.dto.response.LoginResponse;
import com.peddannat.ecommerce.dto.response.UserResponse;
import com.peddannat.ecommerce.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponse registerUser(@RequestBody RegisterRequest request){
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public LoginResponse loginUser(@RequestBody LoginRequest request){
        return userService.loginUser(request);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }
}
