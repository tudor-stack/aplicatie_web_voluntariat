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

    // ----------------------------------------------------
    // METODĂ PRIVATĂ (Principiul DRY)
    // ----------------------------------------------------
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName());
    }

    // ==========================================
    // 1. VOLUNTARUL DĂ RECENZIE EVENIMENTULUI / COMPANIEI
    // ==========================================
    @GetMapping("/volunteer/review-event/{eventId}")
    public String showVolunteerReviewForm(@PathVariable Long eventId, Model model) {
        User volunteer = getCurrentUser();
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null) return "redirect:/my-events";

        // SECURITATE (Prevenire IDOR): A participat voluntarul la acest eveniment?
        Optional<VolunteerApplication> app = applicationRepository.findByVolunteerAndEvent(volunteer, event);
        if (app.isEmpty() || !"ACCEPTED".equals(app.get().getStatus())) {
            return "redirect:/my-events?error=unauthorized_review"; // N-ai fost la eveniment, nu poți da review!
        }

        model.addAttribute("event", event);
        model.addAttribute("targetName", "evenimentul: " + event.getTitle());
        model.addAttribute("postUrl", "/volunteer/submit-review/" + eventId);

        Optional<Review> existingReview = reviewRepository.findByReviewerAndEvent(volunteer, event);

        if (existingReview.isPresent()) {
            model.addAttribute("review", existingReview.get());
            model.addAttribute("isEdit", true);
        } else {
            Review newReview = new Review();
            newReview.setRating(5);
            model.addAttribute("review", newReview);
            model.addAttribute("isEdit", false);
        }
        return "review_form";
    }

    @PostMapping("/volunteer/submit-review/{eventId}")
    public String submitVolunteerReview(@PathVariable Long eventId, @ModelAttribute Review reviewForm) {
        User volunteer = getCurrentUser();
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event != null) {
            // SECURITATE Dublă la Submit
            Optional<VolunteerApplication> app = applicationRepository.findByVolunteerAndEvent(volunteer, event);
            if (app.isEmpty() || !"ACCEPTED".equals(app.get().getStatus())) {
                return "redirect:/my-events?error=unauthorized_review";
            }

            Optional<Review> existingReview = reviewRepository.findByReviewerAndEvent(volunteer, event);

            if (existingReview.isPresent()) {
                Review dbReview = existingReview.get();
                dbReview.setRating(reviewForm.getRating());
                dbReview.setComment(reviewForm.getComment());
                dbReview.setDate(LocalDateTime.now());
                reviewRepository.save(dbReview);
            } else {
                // UPDATE: Nu mai avem type. Destinatarul este User-ul care deține compania organizatoare
                User companyOwner = event.getCompany().getUser();

                Review newReview = new Review(
                        reviewForm.getRating(),
                        reviewForm.getComment(),
                        volunteer,      // Autor: Voluntarul
                        companyOwner,   // Destinatar: Proprietarul ONG-ului
                        event           // Context: Evenimentul
                );
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
        User companyUser = getCurrentUser();
        VolunteerApplication app = applicationRepository.findById(appId).orElse(null);

       if(app == null || !app.getEvent().getCompany().getUser().getId().equals(companyUser.getId())) {
            return "redirect:/company/dashboard?error=unauthorized_review";
        }

       Event event=app.getEvent();
       User volunteer = app.getVolunteer();

        model.addAttribute("event", app.getEvent());
        model.addAttribute("targetName", "voluntarul " + app.getVolunteer().getFirstName());
        model.addAttribute("postUrl", "/company/submit-review/" + appId);

        Optional<Review> existingReview = reviewRepository.findByReviewerAndReviewedUserAndEvent(companyUser, volunteer, event);

        if(existingReview.isPresent()) {
            model.addAttribute("review", existingReview.get());
            model.addAttribute("isEdit", true);
        }
        else{
            Review newReview = new Review();
            newReview.setRating(5);
            model.addAttribute("review", newReview);
            model.addAttribute("isEdit", false);
        }

        return "review_form";
    }

    @PostMapping("/company/submit-review/{appId}")
    public String submitCompanyReview(@PathVariable Long appId, @ModelAttribute Review reviewForm) {
        User companyUser = getCurrentUser();
        VolunteerApplication app = applicationRepository.findById(appId).orElse(null);

        if (app != null && app.getEvent().getCompany().getUser().getId().equals(companyUser.getId())) {

            Event event=app.getEvent();
            User volunteer = app.getVolunteer();

            Optional<Review> existingReview = reviewRepository.findByReviewerAndReviewedUserAndEvent(companyUser, volunteer, event);

            if(existingReview.isPresent()) {
                Review dbReview = existingReview.get();
                dbReview.setRating(reviewForm.getRating());
                dbReview.setComment(reviewForm.getComment());
                dbReview.setDate(LocalDateTime.now());

                reviewRepository.save(dbReview);
            }
            else{
                Review newReview = new Review();
                newReview.setRating(reviewForm.getRating());
                newReview.setComment(reviewForm.getComment());
                newReview.setDate(LocalDateTime.now());

                newReview.setReviewer(companyUser);
                newReview.setEvent(event);
                newReview.setReviewedUser(volunteer);

                reviewRepository.save(newReview);
            }



        }
        return "redirect:/company/dashboard?reviewed";
    }
}