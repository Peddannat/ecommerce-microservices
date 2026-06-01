package com.peddannat.ecommerce.service.impl;

import com.peddannat.ecommerce.dto.request.LoginRequest;
import com.peddannat.ecommerce.dto.request.RegisterRequest;
import com.peddannat.ecommerce.dto.response.LoginResponse;
import com.peddannat.ecommerce.dto.response.UserResponse;
import com.peddannat.ecommerce.entity.User;
import com.peddannat.ecommerce.repository.UserRepository;
import com.peddannat.ecommerce.security.JwtUtil;
import com.peddannat.ecommerce.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl  implements UserService {


    private final UserRepository  userRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository,JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil =jwtUtil;
    }

    @Override
    public UserResponse registerUser(RegisterRequest request) {

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now());

        User savedUser =userRepository.save(user);



        return  new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole());
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new RuntimeException("User not found"));

         if(!user.getPassword().equals(request.getPassword())){
             throw  new RuntimeException("Invalid password");
         }

         String token =jwtUtil.generateToken(user.getEmail(),user.getRole());

        return  new LoginResponse(
                 token,
                 user.getEmail(),
                 user.getRole()
        );
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user= userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found"));

        return  new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

    }

}
