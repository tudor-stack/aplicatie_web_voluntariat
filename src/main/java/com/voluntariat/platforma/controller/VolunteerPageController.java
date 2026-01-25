package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.repository.UserRepository;
import com.voluntariat.platforma.repository.VolunteerApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VolunteerPageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerApplicationRepository applicationRepository;

    @GetMapping("/my-events")
    public String showMyEvents(Model model) {
        // 1. Aflăm cine e logat
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());

        // 2. Cerem bazei de date doar aplicațiile unde status = 'ACCEPTED'
        model.addAttribute("acceptedApps", applicationRepository.findByVolunteerAndStatus(user, "ACCEPTED"));

        return "my_events";
    }
}