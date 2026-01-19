package com.voluntariat.platforma.repository;

import com.voluntariat.platforma.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.voluntariat.platforma.model.User;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Această interfață ne oferă automat metodele: save(), findAll(), findById(), etc.
    Company findByUser(User user);
}
