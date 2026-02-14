package com.voluntariat.platforma.config;

import com.voluntariat.platforma.model.User;
import com.voluntariat.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component        //Uncomment for resetting the password
public class PasswordResetTool implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Resetting the passwords");

        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            String nouaParolaCriptata = passwordEncoder.encode("parola123");
            user.setPassword(nouaParolaCriptata);
        }

        userRepository.saveAll(allUsers);

        System.out.println("All the users( " + allUsers.size() + ") now have the password: 'parola123'.");
    }
}