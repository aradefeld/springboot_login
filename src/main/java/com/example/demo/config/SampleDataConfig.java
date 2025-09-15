package com.example.demo.config;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SampleDataConfig {

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if the sample user already exists
            if (userRepository.findByUsername("jimmy").isEmpty()) {
                User user = new User();
                user.setUsername("jimmy");
                user.setPassword(passwordEncoder.encode("password"));
                user.setFullName("Jimmy Sample");
                user.setEnabled(true);
                userRepository.save(user);
            }
        };
    }
}
