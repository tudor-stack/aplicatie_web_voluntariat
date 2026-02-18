package com.voluntariat.platforma.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {

    @NotBlank(message = "Prenumele este obligatoriu")
    @Size(min=2,max=50,message="Prenumele trebuie sa aiba intre 2 si 50 de caractere")
    private String firstName;

    @NotBlank(message = "Numele de familie este obligatoriu")
    @Size(min=2,max=50,message="Numele trebuie sa aiba intre 2 si 50 de caractere")

    private String lastName;

    @NotBlank(message = "Email-ul este obligatoriu")
    @Email(message = "Format invalid de email")
    private String email;

    @NotBlank(message = "Parola este obligatorie")
    @Size(min = 8, message = "Parola trebuie să aibă minim 8 caractere")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Parola trebuie să conțină litere și cifre")
    private String password;

    private String role;
    private String companyName;
    private String cui;

    // --- Getters și Setters ---
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCui() { return cui; }
    public void setCui(String cui) { this.cui = cui; }
}