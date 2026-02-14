package com.voluntariat.platforma.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception ex, Model model) {
        // Logam eroarea in consola ca sa o vedem noi programatorii
        ex.printStackTrace();

        model.addAttribute("errorMessage", "A apărut o eroare neprevăzută. Te rugăm să încerci mai târziu.");
        model.addAttribute("technicalDetails", ex.getMessage()); // Optional, doar pt development
        return "error/500";
    }
}
