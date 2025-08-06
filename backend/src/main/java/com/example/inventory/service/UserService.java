package com.example.inventory.service;

import com.example.inventory.dto.SignupRequest;
import com.example.inventory.dto.SignupResponse;
import com.example.inventory.model.User;
import com.example.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public SignupResponse registerUser(SignupRequest signupRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return new SignupResponse(false, "Username already exists", null);
        }

        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return new SignupResponse(false, "Email already exists", null);
        }

        // Create new user
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        user.setFullName(signupRequest.getFullName());
        user.setRole(User.UserRole.USER);

        try {
            userRepository.save(user);
            return new SignupResponse(true, "User registered successfully", user.getUsername());
        } catch (Exception e) {
            return new SignupResponse(false, "Registration failed: " + e.getMessage(), null);
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
} 