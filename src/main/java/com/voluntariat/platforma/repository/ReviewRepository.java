package com.voluntariat.platforma.repository;

import com.voluntariat.platforma.model.Event;
import com.voluntariat.platforma.model.Review;
import com.voluntariat.platforma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Găsește recenziile unui eveniment (pentru a vedea ce zic voluntarii)
    List<Review> findByEventAndType(Event event, String type);

    // Verifică dacă userul a dat deja recenzie la acest eveniment (să nu dea de 2 ori)
    boolean existsByReviewerAndEventAndType(User reviewer, Event event, String type);
}