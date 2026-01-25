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

    /// lista de voluntari

    @GetMapping("/company/event/{eventId}/applicants")
    public String viewApplicants(@PathVariable Long eventId, Model model){
        Event event = eventRepository.findById(eventId).orElse(null);

        if(event==null){
            return "redirect:/company/dashboard";
        }

        model.addAttribute("event",event);
        model.addAttribute("applications",event.getApplications());
        return "applicants_list";
    }

    /// lista cu voluntarii enrolled pentru un anumit event

    @GetMapping("/company/event/{eventId}/attendance")
    public String viewAttendance(@PathVariable Long eventId, Model model) {
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null) {
            return "redirect:/company/dashboard";
        }

        // Folosim metoda nouă din Repository: doar cei ACCEPTED
        List<VolunteerApplication> confirmedVolunteers = applicationRepository.findByEventAndStatus(event, "ACCEPTED");

        model.addAttribute("event", event);
        model.addAttribute("participants", confirmedVolunteers);

        return "attendance_list";
    }


    /// dashboard-ul companiei

    @GetMapping("/company/dashboard")
    public String showDashBoard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // --- SPIONI START ---
        System.out.println("=======================================");
        System.out.println("DEBUG: Cine este logat? -> " + email);

        User user = userRepository.findByEmail(email);
        System.out.println("DEBUG: User gasit in baza de date? -> " + (user != null));
        if (user != null) {
            System.out.println("DEBUG: Rolul userului este -> " + user.getRole());
        }

        Company company = companyRepository.findByUser(user);
        System.out.println("DEBUG: Companie gasita? -> " + (company != null));
        // --- SPIONI END ---

        // Aici e paznicul care te dă afară
        if (company == null) {
            System.out.println("DEBUG: PAZNIC: Nu ai companie! Te trimit la Start."); // Mesaj nou
            return "redirect:/";
        }

        model.addAttribute("companyName", company.getCompanyName());
        model.addAttribute("newEvent", new Event());
        model.addAttribute("listaEvenimente", eventRepository.findByCompany(company));
        model.addAttribute("categories", categoryRepository.findAll());


        return "company_dashboard";
    }


    ///Editeaza evenimentul

    @GetMapping("/company/edit-event/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Event event = eventRepository.findById(id).orElse(null);

        // Dacă evenimentul nu există, ne întoarcem la dashboard
        if (event == null) {
            return "redirect:/company/dashboard";
        }

        model.addAttribute("event", event);
        return "edit_event"; // Numele fișierului HTML pe care îl vom crea
    }


    /// stergere eveniment

    @PostMapping("/company/delete-event/{id}")
    public String deleteEvent(@PathVariable Long id) {
        // Căutăm evenimentul
        Event event = eventRepository.findById(id).orElse(null);

        if (event != null) {
            // Ștergerea va declanșa automat ștergerea aplicațiilor și recenziilor (datorită CascadeType.ALL)
            eventRepository.delete(event);
        }

        // Ne întoarcem la Dashboard cu un mesaj de succes
        return "redirect:/company/dashboard?deleted";
    }


    /// adaugarea de evenimente

    @PostMapping("/company/add-event")
    public String addEvent(@ModelAttribute Event event){

        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByEmail(auth.getName());
        Company company=companyRepository.findByUser(user);

        LocalDate minStartDate=LocalDate.now().plusDays(2);
        if(event.getStartDate().isBefore(minStartDate)){
            return "redirect:/company/dashboard?error=date_too_soon";
        }

        if(event.getEndDate().isBefore(event.getStartDate())){
            return "redirect:/company/dashboard?error=end_date_error";
        }

        event.setCompany(company);
        eventRepository.save(event);

        return "redirect:/company/dashboard?success";

    }

    /// updateaza evenimentul



    @PostMapping("/company/update-event")
    public String updateEvent(@ModelAttribute Event event) {
        // Căutăm evenimentul original în baza de date
        // (Este CRITIC să facem asta ca să nu pierdem Compania care a creat evenimentul)
        Event originalEvent = eventRepository.findById(event.getId()).orElse(null);

        if (originalEvent != null) {
            // Actualizăm doar datele care pot fi modificate
            originalEvent.setTitle(event.getTitle());
            originalEvent.setDescription(event.getDescription());
            originalEvent.setStartDate(event.getStartDate());
            originalEvent.setEndDate(event.getEndDate());
            originalEvent.setDuration(event.getDuration());

            // Salvăm originalul actualizat
            eventRepository.save(originalEvent);
        }

        return "redirect:/company/dashboard?updated";
    }

    /// Acceptarea/respingerea voluntarilor
    @PostMapping("/company/application/{appId}/status")
    public String updateStatus(@PathVariable Long appId, @RequestParam String status){
        VolunteerApplication application = applicationRepository.findById(appId).orElse(null);

        if(application!=null){
            application.setStatus(status);
            applicationRepository.save(application);
        }
        return "redirect:/company/event/" +application.getEvent().getId()+"/applicants";
    }


}
