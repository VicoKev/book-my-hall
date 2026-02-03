package com.ifri.bookmyhall.exceptions;

import java.util.stream.Collectors;

import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Gère les exceptions ResourceNotFoundException
     * Levée quand un utilisateur, salle ou réservation n'est pas trouvé
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(
            ResourceNotFoundException ex, 
            Model model,
            RedirectAttributes redirectAttributes) {
        
        log.error("Resource not found: {}", ex.getMessage());
        
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        
        return "redirect:/";
    }

    /**
     * Gère les exceptions IllegalArgumentException
     * Levée pour les données invalides (username existant, email en doublon, etc.)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(
            IllegalArgumentException ex,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        log.error("Illegal argument: {}", ex.getMessage());
        
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/";
    }

    /**
     * Gère les exceptions IllegalStateException
     * Levée pour les opérations impossibles (annuler une réservation terminée, etc.)
     */
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(
            IllegalStateException ex,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        log.error("Illegal state: {}", ex.getMessage());
        
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/";
    }

    /**
     * Gère les erreurs de validation
     * Levée automatiquement par Spring lors de la validation des DTOs
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(
            MethodArgumentNotValidException ex,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Récupérer tous les messages d'erreur de validation
        String errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        log.error("Validation failed: {}", errors);
        
        redirectAttributes.addFlashAttribute("errorMessage", "Erreurs de validation: " + errors);
        return "redirect:/";
    }

    /**
     * Gère toutes les autres exceptions non prévues
     */
    @ExceptionHandler(Exception.class)
    public String handleGlobalException(
            Exception ex,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        log.error("Unexpected error occurred", ex);
        
        redirectAttributes.addFlashAttribute("errorMessage", 
            "Une erreur inattendue s'est produite. Veuillez réessayer.");
        return "redirect:/";
    }
}
