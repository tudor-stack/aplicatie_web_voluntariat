package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.Company;
import com.voluntariat.platforma.model.Event; // <-- Noul import
import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.repository.CompanyRepository;
import com.voluntariat.platforma.repository.EventRepository; // <-- Noul repository
import com.voluntariat.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class CompanyController {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("/company/dashboard")
    public String showDashBoard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // --- SPIONI START ---
        System.out.println("=======================================");
        System.out.println("DEBUG: Cine este logat? -> " + email);

        User user = userRepository.findByEmail(email);
        System.out.println("DEBUG: User gasit in baza de date? -> " + (user != null));
        if (user != null) {
            System.out.println("DEBUG: Rolul userului este -> " + user.getRole());
        }

        Company company = companyRepository.findByUser(user);
        System.out.println("DEBUG: Companie gasita? -> " + (company != null));
        // --- SPIONI END ---

        // Aici e paznicul care te dă afară
        if (company == null) {
            System.out.println("DEBUG: PAZNIC: Nu ai companie! Te trimit la Start."); // Mesaj nou
            return "redirect:/";
        }

        model.addAttribute("companyName", company.getCompanyName());
        model.addAttribute("newEvent", new Event());
        model.addAttribute("listaEvenimente", eventRepository.findByCompany(company));

        return "company_dashboard";
    }

    @PostMapping("/company/add-event")
    public String addEvent(@ModelAttribute Event event){

        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByEmail(auth.getName());
        Company company=companyRepository.findByUser(user);

        event.setCompany(company);
        eventRepository.save(event);

        return "redirect:/company/dashboard?success";

    }


}
