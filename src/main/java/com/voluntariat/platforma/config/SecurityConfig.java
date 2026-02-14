package com.voluntariat.platforma.config;

import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Gasirea userului in DB
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("Utilizatorul nu a fost gasit: " + email);
            }
            // Convertim userul tau in formatul cerut de Spring Security
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole()) // Spring va adauga automat prefixul "ROLE_" intern
                    .build();
        };
    }

    // 3. Configurarea filtrelor de securitate
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**").permitAll() // Rute publice
                        .anyRequest().authenticated()
                )
                // În SecurityConfig.java
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true) // "true" forțează redirecționarea către metoda de mai sus
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}