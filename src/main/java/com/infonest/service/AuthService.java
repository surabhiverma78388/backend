package com.infonest.service;

import com.infonest.model.User;
import com.infonest.repository.UserRepository;
import com.infonest.dto.*;
import com.infonest.config.JwtUtils; // This is for real JWT tokens
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils; // To generate real tokens

    /**
     * Signup Logic
     */
    public String register(SignupRequest request) {
        // 1. Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return "Error: Email already registered!";
        }

        // 2. Map DTO to Entity
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole().toUpperCase()); // Logic: Roles should be uppercase (ADMIN, STUDENT)
        user.setClubId(request.getClubId());

        
        // 3. Encrypt password using BCrypt
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Save to MySQL
        userRepository.save(user);
        return "User registered successfully!";
    }

    /**
     * Login Logic
     */
    public AuthResponse login(LoginRequest request) {
        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: Invalid email or password"));

        // 2. Compare plain password with encrypted password in DB
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            
            // 3. Generate REAL JWT Token
            String token = jwtUtils.generateToken(user.getEmail(), user.getRole());

            // 4. Return data for React redirection
            return new AuthResponse(
                token,
                user.getRole(),
                user.getFirstName(),
                user.getClubId(), // Admin will get 'null' here, which is correct
                 user.getUserId(),
                 user.getEmail()
            );
        } else {
            throw new RuntimeException("Error: Invalid email or password");
        }
    }
}