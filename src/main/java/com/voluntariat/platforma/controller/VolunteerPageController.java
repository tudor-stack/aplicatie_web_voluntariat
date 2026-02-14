package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.model.VolunteerApplication;
import com.voluntariat.platforma.repository.UserRepository;
import com.voluntariat.platforma.repository.VolunteerApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class VolunteerPageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerApplicationRepository applicationRepository;

    @GetMapping("/my-events")
    public String showMyEvents(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());

        List<VolunteerApplication> allApps=applicationRepository.findByVolunteerAndStatus(user,"ACCEPTED");
        LocalDate today=LocalDate.now();

        List<VolunteerApplication> upcoming=new ArrayList<>();
        List<VolunteerApplication> history=new ArrayList<>();

        for(VolunteerApplication app:allApps){
            if(app.getEvent().getStartDate().isBefore(today)){
                history.add(app);
            }else{
                upcoming.add(app);
            }
        }

        model.addAttribute("upcomingApps",upcoming);
        model.addAttribute("historyApps",history);

        return "my_events";
    }
}