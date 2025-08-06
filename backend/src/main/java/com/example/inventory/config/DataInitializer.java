package com.example.inventory.config;

import com.example.inventory.model.User;
import com.example.inventory.repository.UserRepository;
import com.example.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default user if not exists
        if (!userRepository.findByUsername("hitesh").isPresent()) {
            User defaultUser = new User();
            defaultUser.setUsername("hitesh");
            defaultUser.setPassword(passwordEncoder.encode("hitesh33"));
            defaultUser.setEmail("hiteshsonawane33@gmail.com");
            defaultUser.setFullName("Hitesh Sonawane");
            defaultUser.setRole(User.UserRole.ADMIN);

            userRepository.save(defaultUser);
            System.out.println("Default user 'hitesh' created successfully");
        }
    }
} 