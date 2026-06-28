package com.vamshi.stockflow_backend.auth.config;

import com.vamshi.stockflow_backend.user.domain.Role;
import com.vamshi.stockflow_backend.user.domain.User;
import com.vamshi.stockflow_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.existsByEmail("admin@stockflow.com")) {
            return;
        }

        User admin = User.builder()
                .username("admin")
                .email("admin@stockflow.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .active(true)
                .warehouse(null)
                .build();

        userRepository.save(admin);
    }
}