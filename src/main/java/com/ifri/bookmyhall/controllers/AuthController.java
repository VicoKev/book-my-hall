package com.ifri.bookmyhall.controllers;

import java.net.URISyntaxException;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.ifri.bookmyhall.dto.UtilisateurDTO;
import com.ifri.bookmyhall.services.UtilisateurService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
/** Controller pour l'authentification et l'inscription. */
public class AuthController {

    private final UtilisateurService utilisateurService;

    /** Vérifie si l'utilisateur courant est authentifié. */
    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    /** Vérifie si l'utilisateur courant est un administrateur. */
    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    /** Détermine l'URL de redirection après une action d'authentification. */
    private String getBackUrl(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isEmpty())
            return "redirect:" + (isAdmin() ? "/admin/dashboard" : "/user/dashboard");

        try {
            java.net.URI uri = new java.net.URI(referer);
            String path = uri.getPath();
            String contextPath = request.getContextPath();

            if (contextPath != null && !contextPath.isEmpty() && !contextPath.equals("/")
                    && path.startsWith(contextPath)) {
                path = path.substring(contextPath.length());
            }

            if (path.equals("/") || path.equals("/login") || path.equals("/register"))
                return "redirect:" + (isAdmin() ? "/admin/dashboard" : "/user/dashboard");
            return "redirect:" + path;
        } catch (URISyntaxException e) {
            return "redirect:" + (isAdmin() ? "/admin/dashboard" : "/user/dashboard");
        }
    }

    /** Gère l'accès à la page d'accueil ou redirige si authentifié. */
    @GetMapping("/")
    public String home(HttpServletRequest request) {
        if (isAuthenticated())
            return getBackUrl(request);
        return "landing";
    }

    /** Affiche la page de connexion avec messages d'erreur ou déconnexion. */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model, HttpServletRequest request) {

        if (isAuthenticated())
            return getBackUrl(request);
        if (error != null)
            model.addAttribute("errorMessage", "Identifiants incorrects");
        if (logout != null)
            model.addAttribute("successMessage", "Déconnecté avec succès");

        return "login";
    }

    /** Affiche le formulaire d'inscription. */
    @GetMapping("/register")
    public String showRegistrationForm(Model model, HttpServletRequest request) {
        if (isAuthenticated())
            return getBackUrl(request);
        model.addAttribute("utilisateurDTO", new UtilisateurDTO());
        return "register";
    }

    /** Traite la demande d'inscription d'un nouvel utilisateur. */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("utilisateurDTO") UtilisateurDTO dto,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors())
            return "register";
        if (!dto.passwordsMatch()) {
            model.addAttribute("errorMessage", "Les mots de passe ne correspondent pas");
            return "register";
        }

        try {
            utilisateurService.registerUtilisateur(dto);
            log.info("Inscription réussie : {}", dto.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Inscription réussie !");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Erreur inscription {}", dto.getUsername(), e);
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    /** Affiche la page d'accès refusé. */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }

    /** Gère les erreurs 404 globalement. */
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFound(HttpServletRequest request) {
        log.warn("404 détecté : {}", request.getRequestURI());
        return "error/404";
    }

    /** Gère les exceptions non interceptées globalement. */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Erreur critique sur {} : {}", request.getRequestURI(), ex.getMessage());
        return "error/generic";
    }
}
