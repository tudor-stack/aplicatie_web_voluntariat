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

    // 1. Definim PasswordEncoder (pentru a verifica parolele criptate)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. AICI ESTE MAGICUL: Definim UserDetailsService direct aici, fara fisier separat!
    // Aceasta metoda ii spune lui Spring cum sa gaseasca userul in baza ta de date.
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
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        //.usernameParameter("email")
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