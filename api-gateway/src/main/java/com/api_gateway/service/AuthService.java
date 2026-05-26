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
import org.springframework.security.core.userdetails.UserDetails;
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

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }


        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .balance(new BigDecimal("1000.00"))
                .build();

        userRepository.save( user);


        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken  = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                86400L,
                user.getEmail(),
                user.getRole().toString()
        );
    }

    public LoginResponse login(LoginRequest request) {


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String accessToken  = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        User user = userRepository.findByEmail(request.email()).orElseThrow();

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                86400L,
                user.getEmail(),
                user.getRole().toString()
        );
    }
}
