package com.voluntariat.platforma.repository;

import com.voluntariat.platforma.model.VolunteerProfile;
import com.voluntariat.platforma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerProfileRepository extends JpaRepository<VolunteerProfile, Long> {
    // Găsește profilul pe baza userului conectat
    VolunteerProfile findByUser(User user);
}