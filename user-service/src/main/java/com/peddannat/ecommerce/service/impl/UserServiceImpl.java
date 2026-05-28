package com.peddannat.ecommerce.service.impl;

import com.peddannat.ecommerce.model.User;
import com.peddannat.ecommerce.repository.UserRepository;
import com.peddannat.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl  implements UserService {

    @Autowired
    private UserRepository  userRepository;

    @Override
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User loginUser(String email,String password) {

       User user = userRepository.findByEmail(email).orElseThrow(()->
                new RuntimeException("User not found")
        );

       if(!user.getPassword().equals(password)){
           throw new RuntimeException("Invalid password");
       }

       return user;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

}
