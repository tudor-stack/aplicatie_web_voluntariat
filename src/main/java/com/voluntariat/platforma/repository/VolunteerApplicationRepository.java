package com.voluntariat.platforma.repository;

import com.voluntariat.platforma.model.VolunteerApplication;
import com.voluntariat.platforma.model.Event;
import com.voluntariat.platforma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface VolunteerApplicationRepository extends JpaRepository<VolunteerApplication,Long> {

    Optional<VolunteerApplication> findByVolunteerAndEvent(User volunteer, Event event);

}
