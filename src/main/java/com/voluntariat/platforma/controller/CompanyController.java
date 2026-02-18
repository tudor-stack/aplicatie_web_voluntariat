package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.Company;
import com.voluntariat.platforma.model.Event; // <-- Noul import
import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.model.VolunteerApplication;
import com.voluntariat.platforma.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.voluntariat.platforma.controller.VolunteerPageController;
import com.voluntariat.platforma.model.VolunteerApplication;
import com.voluntariat.platforma.repository.VolunteerApplicationRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class CompanyController {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private VolunteerApplicationRepository applicationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Company getCurrentCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        User user=userRepository.findByEmail(email);
        return (user!=null)?companyRepository.findByUser(user):null;
    }


    /// lista de voluntari

    @GetMapping("/company/event/{eventId}/applicants")
    public String viewApplicants(@PathVariable Long eventId, Model model){
        Company currentCompany=getCurrentCompany();

        Event event = eventRepository.findById(eventId).orElse(null);

        if(event==null || !event.getCompany().getId().equals(currentCompany.getId())){
            return "redirect:/company/dashboard?error=access_denied";
        }

        model.addAttribute("event",event);
        model.addAttribute("applications",event.getApplications());
        return "applicants_list";
    }

    /// lista cu voluntarii enrolled pentru un anumit event

    @GetMapping("/company/event/{eventId}/attendance")
    public String viewAttendance(@PathVariable Long eventId, Model model) {
        Company currentCompany = getCurrentCompany();
        Event event = eventRepository.findById(eventId).orElse(null);

        // Security Check
        if (event == null || !event.getCompany().getId().equals(currentCompany.getId())) {
            return "redirect:/company/dashboard?error=access_denied";
        }

        List<VolunteerApplication> confirmedVolunteers = applicationRepository.findByEventAndStatus(event, "ACCEPTED");
        model.addAttribute("event", event);
        model.addAttribute("participants", confirmedVolunteers);
        return "attendance_list";
    }

    // Dashboard (Rămâne aproape la fel, dar mai curat)
    @GetMapping("/company/dashboard")
    public String showDashBoard(Model model) {
        Company company = getCurrentCompany();

        if (company == null) {
            return "redirect:/";
        }

        model.addAttribute("companyName", company.getCompanyName());
        model.addAttribute("newEvent", new Event());
        model.addAttribute("categories", categoryRepository.findAll());

        // --- LOGICA NOUĂ DE FILTRARE ---
        List<Event> allEvents = eventRepository.findByCompany(company);
        LocalDate today = LocalDate.now();

        // 1. Viitoare (Start > Azi)
        List<Event> upcomingEvents = allEvents.stream()
                .filter(e -> e.getStartDate().isAfter(today))
                .toList();

        // 2. În Desfășurare (Start <= Azi ȘI End >= Azi)
        List<Event> ongoingEvents = allEvents.stream()
                .filter(e -> !e.getStartDate().isAfter(today) && !e.getEndDate().isBefore(today))
                .toList();

        // 3. Finalizate (End < Azi)
        List<Event> pastEvents = allEvents.stream()
                .filter(e -> e.getEndDate().isBefore(today))
                .toList();

        model.addAttribute("upcomingEvents", upcomingEvents);
        model.addAttribute("ongoingEvents", ongoingEvents);
        model.addAttribute("pastEvents", pastEvents);

        // Păstrăm și lista completă doar dacă ai nevoie de ea pentru statistici, altfel o putem șterge.
        // model.addAttribute("listaEvenimente", allEvents);

        return "company_dashboard";
    }

    // 3. Formular Editare (SECURIZAT)
    @GetMapping("/company/edit-event/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Company currentCompany = getCurrentCompany();
        Event event = eventRepository.findById(id).orElse(null);

        // Security Check: Nu poți edita evenimentul altcuiva
        if (event == null || !event.getCompany().getId().equals(currentCompany.getId())) {
            return "redirect:/company/dashboard?error=unauthorized";
        }

        model.addAttribute("event", event);
        return "edit_event";
    }

    // 4. Ștergere Eveniment (SECURIZAT - CRITIC!)
    @PostMapping("/company/delete-event/{id}")
    public String deleteEvent(@PathVariable Long id) {
        Company currentCompany = getCurrentCompany();
        Event event = eventRepository.findById(id).orElse(null);

        // Dacă evenimentul există ȘI aparține companiei mele, IL STERGE
        if (event != null && event.getCompany().getId().equals(currentCompany.getId())) {
            eventRepository.delete(event);
        } else {
            // Dacă cineva încearcă să șteargă evenimentul altcuiva
            System.out.println("ALERTĂ SECURITATE: Tentativă de ștergere neautorizată!");
            return "redirect:/company/dashboard?error=fraud_attempt";
        }

        return "redirect:/company/dashboard?deleted";
    }

    // Adăugare Eveniment (Era deja ok, dar folosim metoda helper)
    @PostMapping("/company/add-event")
    public String addEvent(@ModelAttribute Event event) {
        Company company = getCurrentCompany();

        LocalDate minStartDate = LocalDate.now().plusDays(2);
        if (event.getStartDate().isBefore(minStartDate)) {
            return "redirect:/company/dashboard?error=date_too_soon";
        }

        if (event.getEndDate().isBefore(event.getStartDate())) {
            return "redirect:/company/dashboard?error=end_date_error";
        }

        event.setCompany(company); // Setăm proprietarul
        eventRepository.save(event);

        return "redirect:/company/dashboard?success";
    }

    // 5. Update Eveniment (SECURIZAT)
    @PostMapping("/company/update-event")
    public String updateEvent(@ModelAttribute Event event) {
        Company currentCompany = getCurrentCompany();
        Event originalEvent = eventRepository.findById(event.getId()).orElse(null);

        // Verificăm proprietarul înainte de update
        if (originalEvent != null && originalEvent.getCompany().getId().equals(currentCompany.getId())) {

            originalEvent.setTitle(event.getTitle());
            originalEvent.setDescription(event.getDescription());
            originalEvent.setStartDate(event.getStartDate());
            originalEvent.setEndDate(event.getEndDate());
            originalEvent.setDuration(event.getDuration());

            eventRepository.save(originalEvent);
        } else {
            return "redirect:/company/dashboard?error=unauthorized_update";
        }

        return "redirect:/company/dashboard?updated";
    }

    // 6. Update Status Aplicatie (SECURIZAT - TRICKY)
    @PostMapping("/company/application/{appId}/status")
    public String updateStatus(@PathVariable Long appId, @RequestParam String status) {
        Company currentCompany = getCurrentCompany();
        VolunteerApplication application = applicationRepository.findById(appId).orElse(null);

        if (application != null) {
            // Verificare în lanț:
            // Aplicația -> Evenimentul -> Compania Evenimentului == Compania Logată?
            if (application.getEvent().getCompany().getId().equals(currentCompany.getId())) {
                application.setStatus(status);
                applicationRepository.save(application);
            } else {
                return "redirect:/company/dashboard?error=not_your_applicant";
            }
            return "redirect:/company/event/" + application.getEvent().getId() + "/applicants";
        }

        return "redirect:/company/dashboard";
    }
}