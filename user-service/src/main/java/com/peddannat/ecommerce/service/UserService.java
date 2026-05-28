package com.peddannat.ecommerce.service;

import com.peddannat.ecommerce.model.User;

public interface UserService {

    User registerUser(User user);

    User loginUser(String email,String password);

    User getUserById(Long id);

}
