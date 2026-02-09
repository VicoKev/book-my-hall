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
public class ReservationController {

    private final ReservationService reservationService;
    private final SalleService salleService;
    private final UtilisateurService utilisateurService;

    /**
     * Récupère l'utilisateur connecté
     */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    /**
     * Affiche le formulaire de nouvelle réservation
     */
    @GetMapping("/new")
    public String showReservationForm(@RequestParam Long salleId, Model model) {
        log.info("Affichage du formulaire de réservation pour la salle ID: {}", salleId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken
                    || "anonymousUser".equals(auth.getName())) {
                return "redirect:/login";
            }

            SalleDTO salle = salleService.getSalleById(salleId);

            if (!salle.getDisponible()) {
                model.addAttribute("errorMessage", "Cette salle n'est pas disponible");
                return "redirect:/salles/" + salleId;
            }

            String username = getCurrentUsername();
            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(username);

            ReservationDTO reservationDTO = ReservationDTO.builder()
                    .salleId(salleId)
                    .utilisateurId(user.getId())
                    .dateDebut(LocalDate.now().plusDays(1))
                    .build();

            model.addAttribute("reservationDTO", reservationDTO);
            model.addAttribute("salle", salle);
            model.addAttribute("user", user);

            log.debug("Formulaire prêt pour {} réservant {}", username, salle.getNom());

        } catch (Exception e) {
            log.error("Erreur lors du chargement du formulaire de réservation", e);
            return "redirect:/salles";
        }

        return "reservations/new";
    }

    /**
     * Traite la soumission du formulaire de réservation
     */
    @PostMapping("/create")
    public String createReservation(@Valid @ModelAttribute("reservationDTO") ReservationDTO reservationDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Tentative de création de réservation pour la salle ID: {}", reservationDTO.getSalleId());

        if (result.hasErrors()) {
            log.warn("Erreurs de validation dans le formulaire");
            try {
                SalleDTO salle = salleService.getSalleById(reservationDTO.getSalleId());
                model.addAttribute("salle", salle);
            } catch (Exception e) {
                log.error("Erreur lors de la récupération de la salle", e);
            }
            return "reservations/new";
        }

        if (!reservationDTO.hasValidTimeRange()) {
            model.addAttribute("errorMessage", "L'heure de fin doit être après l'heure de début");
            try {
                SalleDTO salle = salleService.getSalleById(reservationDTO.getSalleId());
                model.addAttribute("salle", salle);
            } catch (Exception e) {
                log.error("Erreur lors de la récupération de la salle", e);
            }
            return "reservations/new";
        }

        try {
            ReservationDTO created = reservationService.createReservation(reservationDTO);

            log.info("Réservation créée avec succès: ID {}", created.getId());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Réservation créée avec succès ! En attente de confirmation.");

            return "redirect:/reservations/" + created.getId();

        } catch (IllegalStateException e) {
            log.error("Conflit de réservation: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());

            try {
                SalleDTO salle = salleService.getSalleById(reservationDTO.getSalleId());
                model.addAttribute("salle", salle);
            } catch (Exception ex) {
                log.error("Erreur lors de la récupération de la salle", ex);
            }

            return "reservations/new";

        } catch (Exception e) {
            log.error("Erreur lors de la création de la réservation", e);
            model.addAttribute("errorMessage", "Erreur lors de la création de la réservation");

            try {
                SalleDTO salle = salleService.getSalleById(reservationDTO.getSalleId());
                model.addAttribute("salle", salle);
            } catch (Exception ex) {
                log.error("Erreur lors de la récupération de la salle", ex);
            }

            return "reservations/new";
        }
    }


    /**
     * Affiche les détails d'une réservation
     */
    @GetMapping("/{id}")
    public String detailsReservation(@PathVariable Long id, Model model) {
        log.info("Affichage des détails de la réservation ID: {}", id);

        try {
            ReservationDTO reservation = reservationService.getReservationById(id);

            String username = getCurrentUsername();
            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(username);

            if (!reservation.getUtilisateurId().equals(user.getId()) && !user.isAdmin()) {
                log.warn("Accès refusé à la réservation {} pour {}", id, username);
                return "redirect:/user/reservations";
            }

            model.addAttribute("reservation", reservation);
            model.addAttribute("currentUser", user);

            log.debug("Détails de réservation chargés pour {}", username);

        } catch (Exception e) {
            log.error("Erreur lors du chargement de la réservation ID: {}", id, e);
            return "redirect:/user/reservations";
        }

        return "reservations/details";
    }

    /**
     * Annule une réservation
     */
    @PostMapping("/{id}/annuler")
    public String annulerReservation(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        log.info("Tentative d'annulation de la réservation ID: {}", id);

        try {
            // Vérifier que l'utilisateur a le droit d'annuler
            ReservationDTO reservation = reservationService.getReservationById(id);
            String username = getCurrentUsername();
            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(username);

            if (!reservation.getUtilisateurId().equals(user.getId()) && !user.isAdmin()) {
                log.warn("Tentative d'annulation non autorisée par {}", username);
                redirectAttributes.addFlashAttribute("errorMessage", "Action non autorisée");
                return "redirect:/user/reservations";
            }

            // Annuler la réservation
            reservationService.annulerReservation(id);

            log.info("Réservation {} annulée par {}", id, username);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Réservation annulée avec succès");

        } catch (IllegalStateException e) {
            log.error("Impossible d'annuler la réservation: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Erreur lors de l'annulation de la réservation", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de l'annulation");
        }

        return "redirect:/reservations/" + id;
    }

    /**
     * Confirme une réservation (réservé aux administrateurs)
     */
    @PostMapping("/{id}/confirmer")
    public String confirmerReservation(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        log.info("Tentative de confirmation de la réservation ID: {}", id);

        try {
            reservationService.confirmerReservation(id);

            log.info("Réservation {} confirmée", id);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Réservation confirmée avec succès");

        } catch (Exception e) {
            log.error("Erreur lors de la confirmation de la réservation", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de la confirmation");
        }

        return "redirect:/reservations/" + id;
    }

    /**
     * Vérifie la disponibilité d'une salle pour une date et des horaires donnés
     */
    @GetMapping("/check-availability")
    @ResponseBody
    public String checkAvailability(@RequestParam Long salleId,
            @RequestParam String date,
            @RequestParam String heureDebut,
            @RequestParam String heureFin) {
        // Cette méthode sera appelée en AJAX depuis le formulaire
        // Pour l'instant, on retourne toujours disponible
        // On l'implémentera complètement si besoin
        return "{\"available\": true, \"message\": \"Salle disponible\"}";
    }
}
