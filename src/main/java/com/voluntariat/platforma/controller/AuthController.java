package com.voluntariat.platforma.controller;

import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.voluntariat.platforma.repository.CompanyRepository;
import com.voluntariat.platforma.model.Company;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CompanyRepository companyRepository;
    // 1. RUTA NOUA: Pagina Principala (Home)
    @GetMapping("/")
    public String showHomePage() {
        Authentication auth =SecurityContextHolder.getContext().getAuthentication();

        if(auth !=null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")){
            String email=auth.getName();
            User user=userRepository.findByEmail(email);

            if(user!=null){
                if("ORGANIZATOR".equals(user.getRole())){
                    return "redirect:/company/dashboard";
                }else if ("VOLUNTAR".equals(user.getRole())){
                    return "redirect:/jobs";
                }
            }
        }

        return "homepage";
    }

    // 2. RUTA NOUA: Pagina de Login (Doar vizual momentan)
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Rutele vechi pentru Register raman la fel
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam(required=false) String companyName,
                               @RequestParam (required =false) String cui) {

        /// PT DEBUGG-ing:

        System.out.println("--------Start inregistrare ----------");
        System.out.println("Rol selectat: "+user.getRole());
        System.out.println("NUme firma: "+companyName);
        System.out.println("CUI: "+cui);

        ///

        String parolaCriptata=passwordEncoder.encode(user.getPassword());
        user.setPassword(parolaCriptata);

        userRepository.save(user);
        System.out.println("user salvat cu id: "+user.getId());

        if("ORGANIZATOR".equals(user.getRole())){
            companyRepository.save(new Company(companyName,cui,user));
            System.out.println("company salvat cu succes");
        }

        userRepository.save(user);
        return "redirect:/login?registered";
    }
}