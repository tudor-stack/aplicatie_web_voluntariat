package com.voluntariat.platforma.config;

import com.voluntariat.platforma.model.*;
import com.voluntariat.platforma.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInit {

    @Bean
    CommandLineRunner init(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            CategoryRepository categoryRepository,
            EventRepository eventRepository,
            VolunteerApplicationRepository applicationRepository,
            ReviewRepository reviewRepository,
            VolunteerProfileRepository profileRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            try {
                // Dacă vrei să forțezi regenerarea datelor, șterge manual tabelele sau folosește ddl-auto=create
                if (categoryRepository.count() > 0) {
                    System.out.println(">>> Baza de date are deja date. Sarim peste populare. <<<");
                    return;
                }

                System.out.println(">>> INCEPEM POPULAREA BAZEI DE DATE... <<<");

                // ==========================================
                // 1. CATEGORII
                // ==========================================
                Category catMediu = categoryRepository.save(new Category("Mediu & Natura"));
                Category catEducatie = categoryRepository.save(new Category("Educatie"));
                Category catSocial = categoryRepository.save(new Category("Social & Umanitar"));
                Category catSport = categoryRepository.save(new Category("Sport & Sanatate"));
                Category catAnimale = categoryRepository.save(new Category("Protectia Animalelor"));

                // ==========================================
                // 2. COMPANII (Rol: Company)
                // ==========================================

                // --- Companie 1: Tech Solutions ---
                User uOrg1 = new User("companie@test.com", passwordEncoder.encode("parola123"), "Andrei", "Managerescu", "Company");
                userRepository.save(uOrg1);

                Company comp1 = new Company(
                        "Tech Solutions SRL",
                        "Suntem o companie IT dedicata educatiei digitale.",
                        "www.techsolutions.ro",
                        "Bd. Unirii nr. 10, Bucuresti",
                        "RO123456",
                        uOrg1
                );
                companyRepository.save(comp1);

                // --- Companie 2: Green Earth ONG ---
                User uOrg2 = new User("ong@green.ro", passwordEncoder.encode("parola123"), "Elena", "Verdes", "Company");
                userRepository.save(uOrg2);

                Company comp2 = new Company(
                        "Asociatia Green Earth",
                        "Luptam pentru un mediu curat si paduri sanatoase.",
                        "www.green.org",
                        "Str. Padurii nr. 5, Brasov",
                        "RO999888",
                        uOrg2
                );
                companyRepository.save(comp2);

                // ==========================================
                // 3. VOLUNTARI (Rol: Volunteer)
                // ==========================================

                // --- Voluntar 1: Ion Popescu ---
                User vol1 = new User("voluntar@test.com", passwordEncoder.encode("parola123"), "Ion", "Popescu", "Volunteer");
                userRepository.save(vol1);

                VolunteerProfile prof1 = new VolunteerProfile(
                        vol1, "Ion", "Popescu", "0722123456", "Bucuresti", "1998-05-20", "Java, Engleza, Permis B"
                );
                profileRepository.save(prof1);

                // --- Voluntar 2: Maria Ionescu ---
                User vol2 = new User("maria@test.com", passwordEncoder.encode("parola123"), "Maria", "Ionescu", "Volunteer");
                userRepository.save(vol2);

                VolunteerProfile prof2 = new VolunteerProfile(
                        vol2, "Maria", "Ionescu", "0744000000", "Cluj", "2001-08-15", "Social Media, Organizare, Foto"
                );
                profileRepository.save(prof2);

                // ==========================================
                // 4. EVENIMENTE (Viitoare si Trecute)
                // ==========================================

                // --- VIITOR 1: Hackathon (Peste 10 zile) ---
                Event evFuture1 = new Event(
                        "Hackathon Caritabil 2026",
                        "Codam 48 de ore pentru comunitate.",
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(12),
                        comp1
                );
                evFuture1.setCategory(catEducatie);
                evFuture1.setDuration("48 Ore");
                eventRepository.save(evFuture1);

                // --- VIITOR 2: Plantare (Peste 3 zile) ---
                Event evFuture2 = new Event(
                        "Plantare Copaci Brasov",
                        "Aer curat si fapte bune.",
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(3),
                        comp2
                );
                evFuture2.setCategory(catMediu);
                evFuture2.setDuration("6 Ore");
                eventRepository.save(evFuture2);

                // --- TRECUT 1: Maraton (Acum 20 zile) ---
                Event evPast1 = new Event(
                        "Maratonul Padurii",
                        "Am alergat pentru natura.",
                        LocalDate.now().minusDays(20),
                        LocalDate.now().minusDays(20),
                        comp2
                );
                evPast1.setCategory(catSport);
                evPast1.setDuration("4 Ore");
                eventRepository.save(evPast1);

                // --- TRECUT 2: Workshop IT (Acum 2 luni) ---
                Event evPast2 = new Event(
                        "Workshop Intro IT",
                        "Curs gratuit pentru elevi.",
                        LocalDate.now().minusMonths(2),
                        LocalDate.now().minusMonths(2),
                        comp1
                );
                evPast2.setCategory(catEducatie);
                evPast2.setDuration("2 Ore");
                eventRepository.save(evPast2);


                // ==========================================
                // 5. APLICATII (Istoric vs Viitor)
                // ==========================================

                // --- ION: Aplica la Hackathon (Viitor - PENDING) ---
                // Apare in baza de date, dar nu la "Confirmed" inca
                VolunteerApplication app1 = new VolunteerApplication(vol1, evFuture1);
                applicationRepository.save(app1);

                // --- ION: A participat la Maraton (Trecut - ACCEPTED) ---
                // Ar trebui sa apara la ISTORIC
                VolunteerApplication app2 = new VolunteerApplication(vol1, evPast1);
                app2.setStatus("ACCEPTED");
                applicationRepository.save(app2);

                // --- MARIA: Merge la Plantare (Viitor - ACCEPTED) ---
                // Ar trebui sa apara la URMEAZA SA PARTICIPI
                VolunteerApplication app3 = new VolunteerApplication(vol2, evFuture2);
                app3.setStatus("ACCEPTED");
                applicationRepository.save(app3);

                // --- MARIA: A fost la Workshop (Trecut - ACCEPTED) ---
                // Ar trebui sa apara la ISTORIC
                VolunteerApplication app4 = new VolunteerApplication(vol2, evPast2);
                app4.setStatus("ACCEPTED");
                applicationRepository.save(app4);


                // ==========================================
                // 6. RECENZII
                // ==========================================

                // Ion lasa review la evenimentul trecut
                Review rev1 = new Review(5, "A fost superb! Organizare de nota 10.", vol1, evPast1, "FROM_VOLUNTEER");
                reviewRepository.save(rev1);

                System.out.println(">>> POPULARE COMPLETA: 2 Companii, 2 Voluntari, 2 Evenimente Viitoare, 2 Trecute. <<<");

            } catch (Exception e) {
                System.err.println("!!! EROARE CRITICA LA INITIALIZARE: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}