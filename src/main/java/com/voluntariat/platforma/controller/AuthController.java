package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.dto.UserRegistrationDto; // Import DTO
import com.voluntariat.platforma.model.Company;
import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.repository.CompanyRepository;
import com.voluntariat.platforma.repository.UserRepository;
import jakarta.validation.Valid; // Import validare
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Import erori
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("/")
    public String showHomePage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email);

            if (user != null) {
                // MODIFICARE AICI: Schimbăm din "ORGANIZATOR" în "Company"
                if ("Company".equals(user.getRole())) {
                    return "redirect:/company/dashboard";
                }
                // MODIFICARE AICI: Schimbăm din "VOLUNTAR" în "Volunteer"
                else if ("Volunteer".equals(user.getRole())) {
                    return "redirect:/jobs"; // Sau /events, depinde unde vrei să trimiți voluntarul
                }
            }
        }

        return "homepage";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }


    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "register";
    }


    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto,
                               BindingResult result, Model model) {

        User existingUser=userRepository.findByEmail(userDto.getEmail());
        if(existingUser!=null){
            result.rejectValue("email", "error.user", "email-ul nu este obligatoriu");
        }


        if ("Company".equals(userDto.getRole())) {
            if (userDto.getCompanyName() == null || userDto.getCompanyName().trim().isEmpty()) {
                result.rejectValue("companyName", "error.user", "Numele organizației este obligatoriu.");
                return "register";
            }
            if(userDto.getCui() == null || userDto.getCui().trim().isEmpty()) {
                result.rejectValue("cui", "error.user", "Cui este obligatorii.");
            }
        }

        if(result.hasErrors()){
            return "register";
        }

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(user);

        if ("Company".equals(user.getRole())) {
            Company company = new Company(userDto.getCompanyName(), userDto.getCui(), user);
            companyRepository.save(company);
        }

        return "redirect:/login?registered";
    }
}