package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.Company;
import com.voluntariat.platforma.model.Event;
import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.model.VolunteerApplication;
import com.voluntariat.platforma.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

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
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return (user != null) ? companyRepository.findByUser(user) : null;
    }

    @GetMapping("/company/event/{eventId}/applicants")
    public String viewApplicants(@PathVariable Long eventId, Model model){
        Company currentCompany = getCurrentCompany();
        Event event = eventRepository.findById(eventId).orElse(null);

        if(event == null || !event.getCompany().getId().equals(currentCompany.getId())){
            return "redirect:/company/dashboard?error=access_denied";
        }

        model.addAttribute("event", event);
        model.addAttribute("applications", event.getApplications());
        return "applicants_list";
    }

    @GetMapping("/company/event/{eventId}/attendance")
    public String viewAttendance(@PathVariable Long eventId, Model model) {
        Company currentCompany = getCurrentCompany();
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null || !event.getCompany().getId().equals(currentCompany.getId())) {
            return "redirect:/company/dashboard?error=access_denied";
        }

        List<VolunteerApplication> confirmedVolunteers = applicationRepository.findByEventAndStatus(event, "ACCEPTED");
        model.addAttribute("event", event);
        model.addAttribute("participants", confirmedVolunteers);
        return "attendance_list";
    }

    @GetMapping("/company/dashboard")
    public String showDashBoard(Model model) {
        Company company = getCurrentCompany();
        if (company == null) return "redirect:/";

        model.addAttribute("companyName", company.getCompanyName());
        model.addAttribute("newEvent", new Event());
        model.addAttribute("categories", categoryRepository.findAll());

        List<Event> allEvents = eventRepository.findByCompany(company);
        LocalDate today = LocalDate.now();

        List<Event> upcomingEvents = allEvents.stream().filter(e -> e.getStartDate().isAfter(today)).toList();
        List<Event> ongoingEvents = allEvents.stream().filter(e -> !e.getStartDate().isAfter(today) && !e.getEndDate().isBefore(today)).toList();
        List<Event> pastEvents = allEvents.stream().filter(e -> e.getEndDate().isBefore(today)).toList();

        model.addAttribute("upcomingEvents", upcomingEvents);
        model.addAttribute("ongoingEvents", ongoingEvents);
        model.addAttribute("pastEvents", pastEvents);

        return "company_dashboard";
    }

    @GetMapping("/company/edit-event/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Company currentCompany = getCurrentCompany();
        Event event = eventRepository.findById(id).orElse(null);

        if (event == null || !event.getCompany().getId().equals(currentCompany.getId())) {
            return "redirect:/company/dashboard?error=unauthorized";
        }

        model.addAttribute("event", event);
        return "edit_event";
    }

    @PostMapping("/company/delete-event/{id}")
    public String deleteEvent(@PathVariable Long id) {
        Company currentCompany = getCurrentCompany();
        Event event = eventRepository.findById(id).orElse(null);

        if (event != null && event.getCompany().getId().equals(currentCompany.getId())) {
            eventRepository.delete(event);
        } else {
            return "redirect:/company/dashboard?error=fraud_attempt";
        }
        return "redirect:/company/dashboard?deleted";
    }

    @PostMapping("/company/add-event")
    public String addEvent(@ModelAttribute Event event) {
        Company company = getCurrentCompany();
        LocalDate minStartDate = LocalDate.now().plusDays(2);

        if (event.getStartDate().isBefore(minStartDate)) return "redirect:/company/dashboard?error=date_too_soon";
        if (event.getEndDate().isBefore(event.getStartDate())) return "redirect:/company/dashboard?error=end_date_error";

        event.setCompany(company);
        eventRepository.save(event);
        return "redirect:/company/dashboard?success";
    }

    @PostMapping("/company/update-event")
    public String updateEvent(@ModelAttribute Event eventFromForm) {
        Company currentCompany = getCurrentCompany();
        Event originalEvent = eventRepository.findById(eventFromForm.getId()).orElse(null);

        if (originalEvent == null || !originalEvent.getCompany().getId().equals(currentCompany.getId())) {
            return "redirect:/company/dashboard?error=unauthorized_update";
        }

        if (!eventFromForm.getStartDate().equals(originalEvent.getStartDate())) {
            if (eventFromForm.getStartDate().isBefore(LocalDate.now().plusDays(2))) {
                return "redirect:/company/dashboard?error=update_date_too_soon";
            }
        }

        if (eventFromForm.getEndDate().isBefore(eventFromForm.getStartDate())) {
            return "redirect:/company/dashboard?error=end_date_error";
        }

        originalEvent.setTitle(eventFromForm.getTitle());
        originalEvent.setDescription(eventFromForm.getDescription());
        originalEvent.setStartDate(eventFromForm.getStartDate());
        originalEvent.setEndDate(eventFromForm.getEndDate());
        originalEvent.setDuration(eventFromForm.getDuration());
        originalEvent.setCategory(eventFromForm.getCategory());

        eventRepository.save(originalEvent);
        return "redirect:/company/dashboard?updated";
    }

    @PostMapping("/company/application/{appId}/status")
    public String updateStatus(@PathVariable Long appId, @RequestParam String status) {
        Company currentCompany = getCurrentCompany();
        VolunteerApplication application = applicationRepository.findById(appId).orElse(null);

        if (application != null && application.getEvent().getCompany().getId().equals(currentCompany.getId())) {
            application.setStatus(status);
            applicationRepository.save(application);
            return "redirect:/company/event/" + application.getEvent().getId() + "/applicants";
        }

        return "redirect:/company/dashboard";
    }
}