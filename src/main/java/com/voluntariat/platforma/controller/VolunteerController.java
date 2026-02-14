package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.Event;
import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.model.VolunteerApplication;
import com.voluntariat.platforma.repository.EventRepository;
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

import java.time.LocalDate; // <--- IMPORT CRITIC NOU
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

    @GetMapping("/jobs")
    public String showAllJobs(Model model) {
        // --- MODIFICARE AICI ---
        // În loc de findAll(), cerem doar evenimentele de azi sau din viitor
        List<Event> events = eventRepository.findByStartDateGreaterThanEqual(LocalDate.now());

        model.addAttribute("allEvents", events);
        return "jobs_list";
    }

    @PostMapping("/jobs/apply/{eventId}")
    public String applyToEvent(@PathVariable("eventId") Long eventId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User volunteer = userRepository.findByEmail(auth.getName());

        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent() && volunteer != null) {
            Event event = eventOptional.get();

            // Verificăm dacă a aplicat deja
            Optional<VolunteerApplication> existingApp = volunteerApplicationRepository.findByVolunteerAndEvent(volunteer, event);
            if (existingApp.isPresent()) {
                // E bine să îi spui userului că a aplicat deja
                return "redirect:/jobs?error=already_applied";
            }

            // Creăm aplicația
            VolunteerApplication application = new VolunteerApplication(volunteer, event);
            volunteerApplicationRepository.save(application);

            System.out.println("SUCCES! " + volunteer.getEmail() + " s-a înscris!");
        }

        return "redirect:/jobs?success";
    }
}