package com.scms.controller;

import com.scms.dto.AuthResponse;
import com.scms.dto.LoginRequest;
import com.scms.dto.RegisterRequest;
import com.scms.entity.User;
import com.scms.repository.UserRepository;
import com.scms.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST Controller - handles user registration and login, issues JWT tokens
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> VALID_ROLES = List.of("ROLE_USER", "ROLE_ADMIN");

    // REGISTER API - POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Only accept a role if it's one of the two valid values, else default to ROLE_USER
        String role = (request.getRole() != null && VALID_ROLES.contains(request.getRole()))
                ? request.getRole()
                : "ROLE_USER";
        user.setRole(role);

        userRepository.save(user);
        return new ResponseEntity<>("User registered successfully with role: " + role, HttpStatus.CREATED);
    }

    // LOGIN - POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(request.getUsername(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(token));
    }

}