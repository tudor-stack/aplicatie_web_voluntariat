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
import org.springframework.web.bind.annotation.*;

@Controller
public class ReviewController {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private VolunteerApplicationRepository applicationRepository;

    // 1. VOLUNTARUL DĂ RECENZIE EVENIMENTULUI
    @GetMapping("/volunteer/review-event/{eventId}")
    public String showVolunteerReviewForm(@PathVariable Long eventId, Model model) {
        model.addAttribute("eventId", eventId);
        model.addAttribute("targetName", "acest Eveniment");
        model.addAttribute("postUrl", "/volunteer/submit-review");
        return "review_form";
    }

    @PostMapping("/volunteer/submit-review")
    public String submitVolunteerReview(@RequestParam Long eventId, @RequestParam int rating, @RequestParam String comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User volunteer = userRepository.findByEmail(auth.getName());
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event != null && !reviewRepository.existsByReviewerAndEventAndType(volunteer, event, "FROM_VOLUNTEER")) {
            Review review = new Review(rating, comment, volunteer, event, "FROM_VOLUNTEER");
            reviewRepository.save(review);
        }
        return "redirect:/my-events?reviewed";
    }

    // 2. COMPANIA DĂ RECENZIE VOLUNTARULUI
    // Aici e un truc: Compania dă recenzie "contextului" (aplicației voluntarului la eveniment)
    @GetMapping("/company/review-volunteer/{appId}")
    public String showCompanyReviewForm(@PathVariable Long appId, Model model) {
        VolunteerApplication app = applicationRepository.findById(appId).orElse(null);
        if(app == null) return "redirect:/company/dashboard";

        model.addAttribute("eventId", app.getEvent().getId()); // Legăm de eveniment pentru context
        model.addAttribute("targetId", app.getVolunteer().getId()); // Putem salva ID-ul voluntarului în comentariu sau extinde clasa
        model.addAttribute("targetName", "voluntarul " + app.getVolunteer().getFirstName());
        model.addAttribute("postUrl", "/company/submit-review/" + appId);

        return "review_form";
    }

    @PostMapping("/company/submit-review/{appId}")
    public String submitCompanyReview(@PathVariable Long appId, @RequestParam int rating, @RequestParam String comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User companyUser = userRepository.findByEmail(auth.getName());
        VolunteerApplication app = applicationRepository.findById(appId).orElse(null);

        if (app != null) {
            // Salvăm recenzia legată de eveniment, dar scrisă de companie
            Review review = new Review(rating, comment + " [Pentru voluntarul: " + app.getVolunteer().getEmail() + "]", companyUser, app.getEvent(), "FROM_COMPANY");
            reviewRepository.save(review);
        }
        return "redirect:/company/event/" + app.getEvent().getId() + "/attendance?reviewed";
    }
}