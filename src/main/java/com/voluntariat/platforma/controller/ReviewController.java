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

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class ReviewController {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private VolunteerApplicationRepository applicationRepository;

    // ==========================================
    // 1. VOLUNTARUL DĂ RECENZIE EVENIMENTULUI
    // ==========================================

    @GetMapping("/volunteer/review-event/{eventId}")
    public String showVolunteerReviewForm(@PathVariable Long eventId, Model model) {
        // 1. Identificam voluntarul logat
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User volunteer = userRepository.findByEmail(auth.getName());

        Optional<Event> eventOpt = eventRepository.findById(eventId);

        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            model.addAttribute("event", event);
            model.addAttribute("targetName", "evenimentul: " + event.getTitle());
            // Setăm unde se va trimite formularul
            model.addAttribute("postUrl", "/volunteer/submit-review/" + eventId);

            // 2. Verificăm dacă există deja o recenzie (Edit Mode vs Create Mode)
            Optional<Review> existingReview = reviewRepository.findByReviewerAndEvent(volunteer, event);

            if (existingReview.isPresent()) {
                // UPDATE: Trimitem obiectul existent în HTML
                model.addAttribute("review", existingReview.get());
                model.addAttribute("isEdit", true);
            } else {
                // CREATE: Trimitem un obiect gol
                Review newReview = new Review();
                newReview.setRating(5); // Default 5 stele
                model.addAttribute("review", newReview);
                model.addAttribute("isEdit", false);
            }
            return "review_form";
        }
        return "redirect:/my-events";
    }

    @PostMapping("/volunteer/submit-review/{eventId}")
    public String submitVolunteerReview(@PathVariable Long eventId, @ModelAttribute Review reviewForm) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User volunteer = userRepository.findByEmail(auth.getName());
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event != null) {
            Optional<Review> existingReview = reviewRepository.findByReviewerAndEvent(volunteer, event);

            if (existingReview.isPresent()) {
                // --- LOGICA DE UPDATE (Folosim Setters) ---
                Review dbReview = existingReview.get();
                dbReview.setRating(reviewForm.getRating());
                dbReview.setComment(reviewForm.getComment()); // Update la comentariu
                dbReview.setDate(LocalDateTime.now()); // Update la dată
                reviewRepository.save(dbReview);
            } else {
                // --- LOGICA DE CREATE (Folosim Constructorul Tău) ---
                Review newReview = new Review(
                        reviewForm.getRating(),
                        reviewForm.getComment(),
                        volunteer,  // reviewer
                        event,      // event
                        "FROM_VOLUNTEER" // type
                );
                // Constructorul setează automat data la LocalDateTime.now(), deci e perfect.
                reviewRepository.save(newReview);
            }
        }
        return "redirect:/my-events?reviewed";
    }

    // ==========================================
    // 2. COMPANIA DĂ RECENZIE VOLUNTARULUI
    // ==========================================

    @GetMapping("/company/review-volunteer/{appId}")
    public String showCompanyReviewForm(@PathVariable Long appId, Model model) {
        VolunteerApplication app = applicationRepository.findById(appId).orElse(null);
        if(app == null) return "redirect:/company/dashboard";

        model.addAttribute("event", app.getEvent());
        model.addAttribute("targetName", "voluntarul " + app.getVolunteer().getFirstName());
        model.addAttribute("postUrl", "/company/submit-review/" + appId);

        // La companii simplificăm momentan (doar Create), dar poți extinde logică de mai sus
        Review review = new Review();
        review.setRating(5);
        model.addAttribute("review", review);
        model.addAttribute("isEdit", false);

        return "review_form";
    }

    @PostMapping("/company/submit-review/{appId}")
    public String submitCompanyReview(@PathVariable Long appId, @ModelAttribute Review reviewForm) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User companyUser = userRepository.findByEmail(auth.getName());
        VolunteerApplication app = applicationRepository.findById(appId).orElse(null);

        if (app != null) {
            // Folosim Constructorul pentru a crea recenzia companiei
            // Adăugăm un detaliu în comment despre cine e voluntarul vizat
            String fullComment = reviewForm.getComment() + " [Voluntar vizat: " + app.getVolunteer().getEmail() + "]";

            Review newReview = new Review(
                    reviewForm.getRating(),
                    fullComment,
                    companyUser,      // reviewer (compania)
                    app.getEvent(),   // event (contextul)
                    "FROM_COMPANY"    // type
            );

            reviewRepository.save(newReview);
        }
        return "redirect:/company/dashboard?reviewed";
    }
}