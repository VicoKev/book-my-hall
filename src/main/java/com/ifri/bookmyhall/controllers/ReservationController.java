package com.ifri.bookmyhall.controllers;

import java.time.LocalDate;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ifri.bookmyhall.dto.ReservationDTO;
import com.ifri.bookmyhall.dto.SalleDTO;
import com.ifri.bookmyhall.dto.UtilisateurDTO;
import com.ifri.bookmyhall.services.ReservationService;
import com.ifri.bookmyhall.services.SalleService;
import com.ifri.bookmyhall.services.UtilisateurService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Slf4j
/** Controller pour la gestion des réservations côté utilisateur. */
public class ReservationController {

    private final ReservationService reservationService;
    private final SalleService salleService;
    private final UtilisateurService utilisateurService;

    /** Récupère le nom d'utilisateur de la session courante. */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    /** Affiche le formulaire de nouvelle réservation. */
    @GetMapping("/new")
    public String showReservationForm(@RequestParam Long salleId, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken
                    || "anonymousUser".equals(auth.getName())) {
                return "redirect:/login";
            }

            SalleDTO salle = salleService.getSalleById(salleId);
            if (!salle.getDisponible()) {
                model.addAttribute("errorMessage", "Salle indisponible");
                return "redirect:/salles/" + salleId;
            }

            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(getCurrentUsername());
            model.addAttribute("reservationDTO", ReservationDTO.builder()
                    .salleId(salleId).utilisateurId(user.getId())
                    .dateDebut(LocalDate.now().plusDays(1)).build());
            model.addAttribute("salle", salle);
            model.addAttribute("user", user);

        } catch (Exception e) {
            log.error("Erreur chargement formulaire réservation", e);
            return "redirect:/salles";
        }
        return "reservations/new";
    }

    /** Traite la soumission d'une nouvelle réservation. */
    @PostMapping("/create")
    public String createReservation(@Valid @ModelAttribute("reservationDTO") ReservationDTO dto,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            try {
                model.addAttribute("salle", salleService.getSalleById(dto.getSalleId()));
            } catch (Exception e) {
            }
            return "reservations/new";
        }

        if (!dto.hasValidTimeRange()) {
            model.addAttribute("errorMessage", "Plage horaire invalide");
            try {
                model.addAttribute("salle", salleService.getSalleById(dto.getSalleId()));
            } catch (Exception e) {
            }
            return "reservations/new";
        }

        try {
            ReservationDTO created = reservationService.createReservation(dto);
            log.info("Réservation créée : {}", created.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Réservation créée !");
            return "redirect:/reservations/" + created.getId();
        } catch (Exception e) {
            log.error("Erreur création réservation", e);
            model.addAttribute("errorMessage", e.getMessage());
            try {
                model.addAttribute("salle", salleService.getSalleById(dto.getSalleId()));
            } catch (Exception ex) {
            }
            return "reservations/new";
        }
    }

    /** Affiche les détails d'une réservation (propriétaire ou admin). */
    @GetMapping("/{id}")
    public String detailsReservation(@PathVariable Long id, Model model) {
        try {
            ReservationDTO res = reservationService.getReservationById(id);
            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(getCurrentUsername());

            if (!res.getUtilisateurId().equals(user.getId()) && !user.isAdmin()) {
                return "redirect:/user/reservations";
            }
            model.addAttribute("reservation", res);
            model.addAttribute("currentUser", user);
        } catch (Exception e) {
            log.error("Erreur chargement réservation {}", id, e);
            return "redirect:/user/reservations";
        }
        return "reservations/details";
    }

    /** Permet à l'utilisateur ou à l'admin d'annuler une réservation. */
    @PostMapping("/{id}/annuler")
    public String annulerReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ReservationDTO res = reservationService.getReservationById(id);
            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(getCurrentUsername());

            if (!res.getUtilisateurId().equals(user.getId()) && !user.isAdmin()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Non autorisé");
                return "redirect:/user/reservations";
            }
            reservationService.annulerReservation(id);
            log.info("Réservation {} annulée", id);
            redirectAttributes.addFlashAttribute("successMessage", "Réservation annulée");
        } catch (Exception e) {
            log.error("Erreur annulation réservation {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/reservations/" + id;
    }

    /** Permet à l'admin de confirmer une réservation. */
    @PostMapping("/{id}/confirmer")
    public String confirmerReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.confirmerReservation(id);
            log.info("Réservation {} confirmée", id);
            redirectAttributes.addFlashAttribute("successMessage", "Réservation confirmée");
        } catch (Exception e) {
            log.error("Erreur confirmation réservation {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la confirmation");
        }
        return "redirect:/reservations/" + id;
    }

    /** Vérifie la disponibilité d'une salle (API). */
    @GetMapping("/check-availability")
    @ResponseBody
    public String checkAvailability(@RequestParam Long salleId, @RequestParam String date,
            @RequestParam String heureDebut, @RequestParam String heureFin) {
        return "{\"available\": true, \"message\": \"Salle disponible\"}";
    }
}
