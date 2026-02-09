package com.ifri.bookmyhall.controllers;

import java.net.URISyntaxException;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.ifri.bookmyhall.dto.UtilisateurDTO;
import com.ifri.bookmyhall.services.UtilisateurService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UtilisateurService utilisateurService;

    /**
     * Vérifie si l'utilisateur est connecté
     */
    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    /**
     * Redirige vers le dashboard si pas d'URL précédente
     */
    private String getBackUrl(HttpServletRequest request) {
        String referer = request.getHeader("Referer");

        if (referer == null || referer.isEmpty()) {
            return "redirect:/dashboard";
        }

        try {
            java.net.URI uri = new java.net.URI(referer);
            String path = uri.getPath();
            String contextPath = request.getContextPath();

            if (contextPath != null && !contextPath.isEmpty() && !contextPath.equals("/")
                    && path.startsWith(contextPath)) {
                path = path.substring(contextPath.length());
            }

            if (path.equals("/") || path.equals("/login") || path.equals("/register")) {
                return "redirect:/dashboard";
            }

            return "redirect:" + path;
        } catch (URISyntaxException e) {
            return "redirect:/dashboard";
        }
    }

    /**
     * Affiche la page d'accueil
     */
    @GetMapping("/")
    public String home(HttpServletRequest request) {
        if (isAuthenticated()) {
            return getBackUrl(request);
        }

        return "landing";
    }

    /**
     * Affiche la page de connexion
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model,
            HttpServletRequest request) {

        if (isAuthenticated()) {
            return getBackUrl(request);
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Nom d'utilisateur ou mot de passe incorrect");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Vous êtes déconnecté avec succès");
        }

        return "login";
    }

    /**
     * Affiche le formulaire d'inscription
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model, HttpServletRequest request) {
        if (isAuthenticated()) {
            return getBackUrl(request);
        }

        model.addAttribute("utilisateurDTO", new UtilisateurDTO());
        return "register";
    }

    /**
     * Traite l'inscription d'un nouvel utilisateur
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("utilisateurDTO") UtilisateurDTO utilisateurDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        model.addAttribute("utilisateurDTO", utilisateurDTO);

        if (result.hasErrors()) {
            log.warn("Erreurs de validation pour l'inscription de {}", utilisateurDTO.getUsername());
            return "register";
        }

        if (!utilisateurDTO.passwordsMatch()) {
            model.addAttribute("errorMessage", "Les mots de passe ne correspondent pas");
            return "register";
        }

        try {
            utilisateurService.registerUtilisateur(utilisateurDTO);
            log.info("Inscription réussie pour: {}", utilisateurDTO.getUsername());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de l'inscription: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    /**
     * Affiche la page d'accès refusé
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }

    /**
     * Gère les pages non trouvées (erreur 404)
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFound(HttpServletRequest request) {
        log.warn("Page non trouvée détectée par le handler global: {}", request.getRequestURI());
        return "error/404";
    }

    /**
     * Gère les autres exceptions non capturées
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Exception non capturée pour {}: {}", request.getRequestURI(), ex.getMessage());
        return "error/generic";
    }
}
