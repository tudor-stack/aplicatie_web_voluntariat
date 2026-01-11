package com.voluntariat.platforma.repository;

import com.voluntariat.platforma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Aceasta interfata magica ne ofera automat metode ca:
    // .save(user) -> salveaza
    // .findAll() -> gaseste toti utilizatorii
    // Nu trebuie sa scriem noi cod SQL!
}