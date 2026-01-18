package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. RUTA NOUA: Pagina Principala (Home)
    @GetMapping("/")
    public String showHomePage() {
        return "homepage";
    }

    // 2. RUTA NOUA: Pagina de Login (Doar vizual momentan)
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Rutele vechi pentru Register raman la fel
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        String parolaCriptata = passwordEncoder.encode(user.getPassword());

        user.setPassword(parolaCriptata);

        userRepository.save(user);
        return "redirect:/login?registered";
    }
}