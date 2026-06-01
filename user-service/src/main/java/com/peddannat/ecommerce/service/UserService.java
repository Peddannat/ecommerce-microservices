package com.peddannat.ecommerce.service;

import com.peddannat.ecommerce.dto.request.LoginRequest;
import com.peddannat.ecommerce.dto.request.RegisterRequest;
import com.peddannat.ecommerce.dto.response.LoginResponse;
import com.peddannat.ecommerce.dto.response.UserResponse;

public interface UserService {

    UserResponse registerUser(RegisterRequest request);

    LoginResponse loginUser(LoginRequest request);

    UserResponse getUserById(Long id);

}
