package com.voluntariat.platforma.config;

import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("Utilizatorul nu a fost gasit: " + email);
            }
            // MODIFICAREA 1: .toUpperCase()
            // Transformam "Company" -> "COMPANY".
            // Astfel, Spring va crea rolul "ROLE_COMPANY" indiferent cum e scris in baza de date.
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole().toUpperCase())
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**").permitAll()
                        // MODIFICAREA 2: Reguli clare de acces
                        // Doar cine are ROLE_COMPANY intra la /company/**
                        .requestMatchers("/company/**").hasRole("COMPANY")
                        // Doar cine are ROLE_VOLUNTEER intra la /jobs sau aplicari
                        .requestMatchers("/jobs/**", "/volunteer/**").hasRole("VOLUNTEER") // SAU .hasAnyRole("VOLUNTEER", "COMPANY") daca si companiile au voie sa vada joburile
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        // MODIFICAREA 3: Redirecționare inteligentă (Logic Handler)
                        // In loc sa ii trimitem pe toti la /jobs, verificam cine sunt.
                        .successHandler((request, response, authentication) -> {
                            var roles = authentication.getAuthorities();
                            String redirectUrl = "/jobs"; // Default pentru voluntari

                            // Verificam daca userul are rolul de companie
                            if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_COMPANY"))) {
                                redirectUrl = "/company/dashboard";
                            }

                            response.sendRedirect(redirectUrl);
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .expiredUrl("/login?expired")
                );

        return http.build();
    }
}