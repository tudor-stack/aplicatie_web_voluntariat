package com.voluntariat.platforma.repository;

import com.voluntariat.platforma.model.Event;
import com.voluntariat.platforma.model.Review;
import com.voluntariat.platforma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByEventAndReviewer_Role(Event event, String role);
    boolean existsByReviewerAndEvent(User reviewer, Event event);

    Optional<Review> findByReviewerAndEvent(User reviewer, Event event);

    List<Review> findByEvent(Event event);
    List<Review> findByReviewedUser(User reviewedUser);
    List<Review> findByReviewer(User reviewer);
}