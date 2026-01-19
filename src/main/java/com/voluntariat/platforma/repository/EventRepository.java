package com.voluntariat.platforma.repository;

import com.voluntariat.platforma.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voluntariat.platforma.model.Company;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long>{
    List <Event> findByCompany(Company company);
}
