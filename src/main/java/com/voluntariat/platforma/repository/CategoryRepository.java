package com.voluntariat.platforma.repository;

import com.voluntariat.platforma.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}