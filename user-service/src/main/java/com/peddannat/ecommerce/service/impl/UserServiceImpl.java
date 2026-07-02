package com.peddannat.ecommerce.service.impl;

import com.peddannat.ecommerce.dto.request.LoginRequest;
import com.peddannat.ecommerce.dto.request.RegisterRequest;
import com.peddannat.ecommerce.dto.response.LoginResponse;
import com.peddannat.ecommerce.dto.response.UserResponse;
import com.peddannat.ecommerce.entity.Role;
import com.peddannat.ecommerce.entity.User;
import com.peddannat.ecommerce.exception.DuplicateResourceException;
import com.peddannat.ecommerce.exception.InvalidCredentialsException;
import com.peddannat.ecommerce.exception.ResourceNotFoundException;
import com.peddannat.ecommerce.repository.UserRepository;
import com.peddannat.ecommerce.security.JwtUtil;
import com.peddannat.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserServiceImpl  implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository  userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserResponse registerUser(RegisterRequest request) {

        log.info("Registration attempt for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {

            log.warn("Duplicate registration attempt for email: {}", request.getEmail());

            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        // Create a new user entity from the incoming registration request.
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser =userRepository.save(user);
        return  mapToUserResponse(savedUser);
    }


    @Override
    public LoginResponse loginUser(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Find the user by email and validate login credentials.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->{
                    log.warn("User not found for email: {}", request.getEmail());
                    return new ResourceNotFoundException("User not found with email: " + request.getEmail());
                });

         if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
             log.warn("Invalid password attempt for email: {}", request.getEmail());
             throw  new InvalidCredentialsException("Invalid email or password");
         }

        // Generate JWT token after successful authentication.
        String token =jwtUtil.generateToken(user.getEmail(),user.getRole().name());

        log.info("Login successful for user id: {}", user.getId());

        return  new LoginResponse(
                 token,
                 user.getEmail(),
                 user.getRole()
        );
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by id: {}", id);

        User user= userRepository.findById(id)
                .orElseThrow(()-> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        return  mapToUserResponse(user);

    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

}
