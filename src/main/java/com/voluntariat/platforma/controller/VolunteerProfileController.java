package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.model.VolunteerProfile;
import com.voluntariat.platforma.repository.UserRepository;
import com.voluntariat.platforma.repository.VolunteerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class VolunteerProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerProfileRepository volunteerProfileRepository;

    // 1. Afișează pagina de profil
    @GetMapping("/volunteer/profile")
    public String showProfileForm(Model model) {
        // Luăm userul curent
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());

        // Căutăm profilul existent
        VolunteerProfile profile = volunteerProfileRepository.findByUser(user);

        // Dacă nu există, creăm unul gol ca să nu dea eroare formularul
        if (profile == null) {
            profile = new VolunteerProfile();
            profile.setUser(user); // Îl legăm de user
        }

        model.addAttribute("profile", profile);
        return "volunteer_profile"; // Numele fișierului HTML
    }

    // 2. Salvează datele din formular
    @PostMapping("/volunteer/save-profile")
    public String saveProfile(@ModelAttribute VolunteerProfile profile) {
        // Trebuie să ne asigurăm că setăm userul corect (security check)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());

        // Dacă profilul exista deja în bază, trebuie să îi punem ID-ul ca să facă UPDATE, nu INSERT
        VolunteerProfile existingProfile = volunteerProfileRepository.findByUser(user);
        if (existingProfile != null) {
            profile.setId(existingProfile.getId());
        }

        profile.setUser(user); // Ne asigurăm că rămâne legat de userul corect
        volunteerProfileRepository.save(profile);

        return "redirect:/jobs?profileUpdated"; // Ne întoarcem la lista de joburi
    }
}