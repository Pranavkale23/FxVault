package com.wallet.controller;

import com.wallet.config.JwtUtil;
import com.wallet.dto.AuthRequest;
import com.wallet.dto.AuthResponse;
import com.wallet.model.User;
import com.wallet.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthController(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest()
                .body("Email already registered");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        userRepo.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                req.getEmail(), req.getPassword())
        );
        String token = jwtUtil.generateToken(req.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, req.getEmail()));
    }
}