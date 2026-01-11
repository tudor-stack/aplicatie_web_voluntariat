package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired // Spring ne aduce automat repository-ul aici
    private UserRepository userRepository;

    // Afisarea paginii (GET)
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Primirea datelor din formular (POST)
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        // 1. Salvam utilizatorul in baza de date
        userRepository.save(user);

        // 2. Afisam in consola ca a reusit (pentru verificare)
        System.out.println("Utilizator nou salvat: " + user.getEmail());

        // 3. Redirectionam utilizatorul catre pagina de inregistrare din nou (sau login pe viitor)
        // param?success inseamna ca vom putea afisa un mesaj de succes
        return "redirect:/register?success";
    }
}