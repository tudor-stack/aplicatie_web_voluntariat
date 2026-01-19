package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.Event;
import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.model.VolunteerApplication; // Entitatea ta nouă
import com.voluntariat.platforma.repository.EventRepository;
import com.voluntariat.platforma.repository.UserRepository; // <-- Avem nevoie de asta
import com.voluntariat.platforma.repository.VolunteerApplicationRepository; // <-- Și de asta

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
        List<Event> events = eventRepository.findAll();
        model.addAttribute("allEvents", events);
        return "jobs_list";
    }

    @PostMapping("/jobs/apply/{eventId}")
    public String applyToEvent(@PathVariable("eventId") Long eventId) {

        ///find the logged volunteer
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        User volunteer =userRepository.findByEmail(auth.getName());

        /// we find the event
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if(eventOptional.isPresent()&& volunteer!=null) {
            Event event=eventOptional.get();

            /// we check for multiple applications
            Optional<VolunteerApplication>existingApp= volunteerApplicationRepository.findByVolunteerAndEvent(volunteer, event);
            if(existingApp.isPresent()) {
                return "redirect:/jobs?error=already_applied";
            }

            /// we create the new application
            VolunteerApplication application=new VolunteerApplication(volunteer,event);

            volunteerApplicationRepository.save(application);
            System.out.println("SUCCES! "+ volunteer.getEmail()+ " enrolled!");


        }

        return "redirect:/jobs?success";
    }

}
