package com.vamshi.stockflow_backend.auth.service.impl;

import com.vamshi.stockflow_backend.auth.dto.*;
import com.vamshi.stockflow_backend.auth.service.AuthService;
import com.vamshi.stockflow_backend.common.exception.EmailAlreadyExistsException;
import com.vamshi.stockflow_backend.common.exception.InvalidCredentialsException;
import com.vamshi.stockflow_backend.common.exception.UsernameAlreadyExistsException;
import com.vamshi.stockflow_backend.security.jwt.JwtUtil;
import com.vamshi.stockflow_backend.user.domain.Role;
import com.vamshi.stockflow_backend.user.domain.User;
import com.vamshi.stockflow_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl
        implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
                        throw new EmailAlreadyExistsException(request.getEmail());
        }

        if (userRepository.existsByUsername(request.getUsername())) {
                        throw new UsernameAlreadyExistsException(request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(Role.CUSTOMER)
                .active(true)
                .warehouse(null)
                .build();

        userRepository.save(user);

        String token =
                jwtUtil.generateToken(
                        user.getEmail(),
                        user.getRole().name()
                );

        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getUsername()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (AuthenticationException exception) {

            throw new InvalidCredentialsException();
        }

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        String token =
                jwtUtil.generateToken(
                        user.getEmail(),
                        user.getRole().name()
                );

        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getUsername()
        );
    }
}