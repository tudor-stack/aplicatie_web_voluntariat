package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.Event;
import com.voluntariat.platforma.model.Review;
import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.model.VolunteerApplication;
import com.voluntariat.platforma.repository.EventRepository;
import com.voluntariat.platforma.repository.ReviewRepository;
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
import java.util.stream.Collectors;

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

    // ---------------------------------------------------------
    // LISTA DE JOBURI (FEED PRINCIPAL)
    // ---------------------------------------------------------
    @GetMapping("/jobs")
    public String showAllJobs(Model model) {
        // 1. Luăm toate evenimentele viitoare disponibile
        List<Event> allEvents = eventRepository.findByStartDateGreaterThanEqual(LocalDate.now());

        // 2. Identificăm utilizatorul curent
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verificăm dacă userul este logat și nu este "anonymousUser"
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {

            User currentUser = userRepository.findByEmail(auth.getName());

            if (currentUser != null) {
                // 3. Aflăm ID-urile evenimentelor unde userul A APLICAT DEJA
                // (Indiferent dacă e PENDING, ACCEPTED sau REJECTED - nu vrem să le mai vadă în feed)
                List<Long> appliedEventIds = volunteerApplicationRepository.findByVolunteer(currentUser)
                        .stream()
                        .map(app -> app.getEvent().getId()) // Luăm doar ID-ul evenimentului
                        .collect(Collectors.toList());

                // 4. FILTRARE: Păstrăm doar evenimentele unde ID-ul NU este în lista celor aplicate
                allEvents = allEvents.stream()
                        .filter(event -> !appliedEventIds.contains(event.getId()))
                        .collect(Collectors.toList());
            }
        }

        model.addAttribute("allEvents", allEvents);
        return "jobs_list";
    }

    // ---------------------------------------------------------
    // DETALII JOB + LOGICA DE RECENZII
    // ---------------------------------------------------------
    @GetMapping("/jobs/details/{id}")
    public String showJobDetails(@PathVariable Long id, Model model) throws ResourceNotFoundException {
        // 1. Găsim evenimentul
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evenimentul nu există!"));

        model.addAttribute("event", event);

        // 2. Logică terminare eveniment
        boolean isFinished = event.getEndDate().isBefore(LocalDate.now());
        model.addAttribute("isFinished", isFinished);

        // 3. Recenzii (doar dacă s-a terminat)
        if (isFinished) {
            List<Review> reviews = reviewRepository.findByEvent(event);
            model.addAttribute("reviews", reviews);
        } else {
            model.addAttribute("reviews", new ArrayList<>());
        }

        // 4. VERIFICARE STATUS APLICARE (Cod Nou)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String applicationStatus = null; // Default: nu a aplicat

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            User currentUser = userRepository.findByEmail(auth.getName());
            model.addAttribute("currentUserId", currentUser.getId());

            // Căutăm dacă există o aplicație între acest user și acest eveniment
            Optional<VolunteerApplication> existingApp = volunteerApplicationRepository.findByVolunteerAndEvent(currentUser, event);

            if (existingApp.isPresent()) {
                applicationStatus = existingApp.get().getStatus(); // Va fi "PENDING", "ACCEPTED" sau "REJECTED"
            }
        } else {
            model.addAttribute("currentUserId", -1L);
        }

        // Trimitem statusul în HTML. Dacă e null, înseamnă că nu a aplicat.
        model.addAttribute("applicationStatus", applicationStatus);

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

            // Dacă a aplicat deja, îl trimitem înapoi cu eroare (deși butonul ar trebui să nu mai apară în feed)
            if (existingApp.isPresent()) {
                return "redirect:/jobs?error=already_applied";
            }

            // Implicit statusul este PENDING (setat în constructor sau default în DB)
            VolunteerApplication application = new VolunteerApplication(volunteer, event);
            volunteerApplicationRepository.save(application);

            System.out.println("SUCCES! " + volunteer.getEmail() + " s-a înscris (Pending)!");
        }

        return "redirect:/jobs?success";
    }
}