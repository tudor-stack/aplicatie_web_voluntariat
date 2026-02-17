package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.Event;
import com.voluntariat.platforma.model.Review; // <--- Import Review
import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.model.VolunteerApplication;
import com.voluntariat.platforma.repository.EventRepository;
import com.voluntariat.platforma.repository.ReviewRepository; // <--- Import ReviewRepository
import com.voluntariat.platforma.repository.UserRepository;
import com.voluntariat.platforma.repository.VolunteerApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.voluntariat.platforma.exception.ResourceNotFoundException;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class VolunteerController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;


    @GetMapping("/jobs")
    public String showAllJobs(Model model) {
        List<Event> events = eventRepository.findByStartDateGreaterThanEqual(LocalDate.now());
        model.addAttribute("allEvents", events);
        return "jobs_list";
    }

    // ---------------------------------------------------------
    // DETALII JOB + LOGICA DE RECENZII
    // ---------------------------------------------------------
    @GetMapping("/jobs/details/{id}")
    public String showJobDetails(@PathVariable Long id, Model model) throws ResourceNotFoundException {
        // 1. Găsim evenimentul
        Event event = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Evenimentul cu ID-ul " + id + " nu a fost găsit!"));

        model.addAttribute("event", event);

        // 2. LOGICA DE TIMP:

        boolean isFinished = event.getEndDate().isBefore(LocalDate.now());
        model.addAttribute("isFinished", isFinished);

        // 3. RECENZII:
        if (isFinished) {
            List<Review> reviews = reviewRepository.findByEvent(event);
            model.addAttribute("reviews", reviews);
        } else {

            model.addAttribute("reviews", new ArrayList<>());
        }

        // 4. IDENTITATE:
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            User currentUser = userRepository.findByEmail(auth.getName());
            model.addAttribute("currentUserId", currentUser.getId());
        } else {
            model.addAttribute("currentUserId", -1L);
        }

        // Returnăm pagina HTML de detalii
        return "job_details";
    }

    // ---------------------------------------------------------
    // APLICARE LA JOB
    // ---------------------------------------------------------
    @PostMapping("/jobs/apply/{eventId}")
    public String applyToEvent(@PathVariable("eventId") Long eventId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User volunteer = userRepository.findByEmail(auth.getName());

        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent() && volunteer != null) {
            Event event = eventOptional.get();

            Optional<VolunteerApplication> existingApp = volunteerApplicationRepository.findByVolunteerAndEvent(volunteer, event);
            if (existingApp.isPresent()) {
                return "redirect:/jobs?error=already_applied";
            }

            VolunteerApplication application = new VolunteerApplication(volunteer, event);
            volunteerApplicationRepository.save(application);

            System.out.println("SUCCES! " + volunteer.getEmail() + " s-a înscris!");
        }

        return "redirect:/jobs?success";
    }
}