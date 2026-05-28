package com.api_gateway.service;

import com.api_gateway.dto.LoginRequest;
import com.api_gateway.dto.LoginResponse;
import com.api_gateway.dto.RegisterRequest;
import com.api_gateway.entity.Role;
import com.api_gateway.entity.User;
import com.api_gateway.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;



@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    public LoginResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email()))
            throw new RuntimeException("Email already registered");

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .balance(new BigDecimal("1000.00"))
                .build();

        User savedUser = userRepository.save(user); // ← use savedUser (has ID now)

        // ✅ Pass User entity directly — not UserDetails
        String accessToken  = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser.getEmail());

        return new LoginResponse(
                accessToken, refreshToken, "Bearer",
                86400L, savedUser.getEmail(), savedUser.getRole().toString()
        );
    }

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // ✅ Fetch User entity from DB — not UserDetails
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new LoginResponse(
                accessToken, refreshToken, "Bearer",
                86400L, user.getEmail(), user.getRole().toString()
        );
    }

}
