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
                System.out.println(">>> START INITIALIZARE DATABASE CU DATE COMPLEXE <<<");

                // Verificam daca exista deja date pentru a evita duplicatele
                if (userRepository.findByEmail("companie@test.com") != null) {
                    System.out.println(">>> Datele exista deja. Sarim peste populare. <<<");
                    return;
                }

                // ==========================================
                // 1. CATEGORII
                // ==========================================
                Category catMediu = categoryRepository.save(new Category("Mediu & Natura"));
                Category catEducatie = categoryRepository.save(new Category("Educatie"));
                Category catSocial = categoryRepository.save(new Category("Social & Umanitar"));
                Category catSport = categoryRepository.save(new Category("Sport & Sanatate"));
                Category catAnimale = categoryRepository.save(new Category("Protectia Animalelor"));

                // ==========================================
                // 2. COMPANII & ORGANIZATORI
                // ==========================================

                // --- Companie 1: Tech Solutions ---
                User uOrg1 = new User("companie@test.com", passwordEncoder.encode("parola123"), "Andrei", "Managerescu", "ORGANIZATOR");
                userRepository.save(uOrg1);

                Company comp1 = new Company(
                        "Tech Solutions SRL",
                        "Suntem o companie IT dedicata educatiei digitale.", // Description
                        "www.techsolutions.ro", // Website
                        "Bd. Unirii nr. 10, Bucuresti", // Address
                        "RO123456", // CUI
                        uOrg1 // User
                );
                companyRepository.save(comp1);

                // --- Companie 2: Green Earth ONG ---
                User uOrg2 = new User("ong@green.ro", passwordEncoder.encode("parola123"), "Elena", "Verdes", "ORGANIZATOR");
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
                // 3. VOLUNTARI & PROFILURI
                // ==========================================

                // --- Voluntar 1: Ion (Profil Complet) ---
                User vol1 = new User("voluntar@test.com", passwordEncoder.encode("parola123"), "Ion", "Popescu", "VOLUNTAR");
                userRepository.save(vol1);

                VolunteerProfile prof1 = new VolunteerProfile(
                        vol1,
                        "Ion", "Popescu",
                        "0722123456",
                        "Bucuresti",
                        "1998-05-20",
                        "Java, Spring Boot, Engleza Avansat, Permis B"
                );
                profileRepository.save(prof1);

                // --- Voluntar 2: Maria (Profil Complet) ---
                User vol2 = new User("maria@test.com", passwordEncoder.encode("parola123"), "Maria", "Ionescu", "VOLUNTAR");
                userRepository.save(vol2);

                VolunteerProfile prof2 = new VolunteerProfile(
                        vol2,
                        "Maria", "Ionescu",
                        "0744987654",
                        "Cluj-Napoca",
                        "2002-10-15",
                        "Organizare Evenimente, Public Speaking, Social Media"
                );
                profileRepository.save(prof2);

                // --- Voluntar 3: Alex (Fara Profil inca) ---
                User vol3 = new User("alex@test.com", passwordEncoder.encode("parola123"), "Alex", "Dima", "VOLUNTAR");
                userRepository.save(vol3);


                // ==========================================
                // 4. EVENIMENTE
                // ==========================================

                // Ev 1: Hackathon (Tech Solutions) - Viitor
                Event ev1 = new Event(
                        "Hackathon Caritabil",
                        "Codam 48 de ore pentru a ajuta ONG-urile locale. Mancare asigurata!",
                        LocalDate.now().plusDays(10), // Start
                        LocalDate.now().plusDays(12), // End
                        comp1
                );
                ev1.setCategory(catEducatie);
                ev1.setDuration("48 Ore");
                eventRepository.save(ev1);

                // Ev 2: Plantare Copaci (Green Earth) - Viitor Apropiat
                Event ev2 = new Event(
                        "Plantare la Brasov",
                        "Vino sa plantezi un copac si sa respiri aer curat.",
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(3),
                        comp2
                );
                ev2.setCategory(catMediu);
                ev2.setDuration("6 Ore");
                eventRepository.save(ev2);

                // Ev 3: Maraton (Green Earth) - TRECUT
                Event ev3 = new Event(
                        "Maratonul Padurii",
                        "Alergam pentru natura. Fondurile merg catre rezervatii.",
                        LocalDate.now().minusDays(20),
                        LocalDate.now().minusDays(20),
                        comp2
                );
                ev3.setCategory(catSport);
                ev3.setDuration("4 Ore");
                eventRepository.save(ev3);


                // ==========================================
                // 5. APLICATII (Statusuri diferite)
                // ==========================================

                // Ion aplica la Hackathon (PENDING)
                VolunteerApplication app1 = new VolunteerApplication(vol1, ev1);
                // Status default e PENDING din constructor
                applicationRepository.save(app1);

                // Maria aplica la Plantare (ACCEPTED)
                VolunteerApplication app2 = new VolunteerApplication(vol2, ev2);
                app2.setStatus("ACCEPTED");
                applicationRepository.save(app2);

                // Alex aplica la Plantare (REJECTED)
                VolunteerApplication app3 = new VolunteerApplication(vol3, ev2);
                app3.setStatus("REJECTED");
                applicationRepository.save(app3);

                // Ion a participat la Maratonul din trecut (ACCEPTED)
                VolunteerApplication app4 = new VolunteerApplication(vol1, ev3);
                app4.setStatus("ACCEPTED");
                applicationRepository.save(app4);


                // ==========================================
                // 6. RECENZII (Review-uri)
                // ==========================================

                // Ion lasa review la Maraton (unde a participat)
                Review rev1 = new Review(
                        5,
                        "O organizare excelenta! Traseul a fost superb.",
                        vol1,
                        ev3,
                        "FROM_VOLUNTEER"
                );
                reviewRepository.save(rev1);

                // Maria lasa review la Plantare (unde urmeaza sa mearga)
                Review rev2 = new Review(
                        4,
                        "Abia astept evenimentul! Comunicarea a fost buna.",
                        vol2,
                        ev2,
                        "FROM_VOLUNTEER"
                );
                reviewRepository.save(rev2);

                System.out.println(">>> BAZA DE DATE A FOST POPULATA CU SUCCES! <<<");

            } catch (Exception e) {
                System.err.println("!!! EROARE CRITICA LA INITIALIZARE: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}