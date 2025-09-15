package com.example.demo.config;

import com.example.demo.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // Ignore static resources and H2 console
    // Configure static resources and H2 console access
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(
                "/css/**",
                "/js/**",
                "/webjars/**"
            );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure authorization
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(
                    "/",
                    "/home",
                    "/login",
                    "/h2-console/**",
                    "/css/**",
                    "/js/**",
                    "/webjars/**"
                ).permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Configure form login
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            
            // Configure logout
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            
            // Configure CSRF - enable with H2 console exception
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            
            // Configure headers and frame options
            .headers(headers -> headers
                .frameOptions(frame -> frame
                    .sameOrigin()
                    .disable()  // Required for H2 console
                )
            )
            
            // Configure exception handling
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }

    // Custom AuthenticationManager as a bean
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
}
