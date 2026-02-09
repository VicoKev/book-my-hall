package com.ifri.bookmyhall.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ifri.bookmyhall.dto.ReservationDTO;
import com.ifri.bookmyhall.dto.UtilisateurDTO;
import com.ifri.bookmyhall.models.Reservation.StatutReservation;
import com.ifri.bookmyhall.services.ReservationService;
import com.ifri.bookmyhall.services.UtilisateurService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UtilisateurService utilisateurService;
    private final ReservationService reservationService;

    /**
     * Récupère le nom d'utilisateur de l'utilisateur connecté
     */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    /**
     * Affiche le dashboard de l'utilisateur
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String username = getCurrentUsername();
        log.info("Accès au dashboard de l'utilisateur: {}", username);

        try {
            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(username);
            model.addAttribute("user", user);

            Page<ReservationDTO> reservationsPage = reservationService.getReservationsByUtilisateur(user.getId(),
                    PageRequest.of(0, 5));
            model.addAttribute("reservations", reservationsPage.getContent());

            Page<ReservationDTO> reservationsFuturesPage = reservationService
                    .getFutureReservationsByUtilisateur(user.getId(), PageRequest.of(0, 5));
            model.addAttribute("reservationsFutures", reservationsFuturesPage.getContent());

            long totalReservations = reservationsPage.getTotalElements();
            long reservationsEnCours = reservationsPage.getContent().stream()
                    .filter(r -> r.getStatut() != null)
                    .filter(r -> r.getStatut() != StatutReservation.CANCELLED)
                    .filter(r -> r.getStatut() != StatutReservation.COMPLETED)
                    .count();

            model.addAttribute("totalReservations", totalReservations);
            model.addAttribute("reservationsEnCours", reservationsEnCours);

            log.debug("Dashboard chargé pour {}: {} réservations totales, {} en cours",
                    username, totalReservations, reservationsEnCours);

        } catch (Exception e) {
            log.error("Erreur lors du chargement du dashboard pour {}", username, e);
            model.addAttribute("errorMessage", "Erreur lors du chargement du dashboard");
        }

        return "user/dashboard";
    }

    /**
     * Affiche le profil de l'utilisateur
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        String username = getCurrentUsername();
        log.info("Accès au profil de l'utilisateur: {}", username);

        try {
            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(username);
            model.addAttribute("user", user);
        } catch (Exception e) {
            log.error("Erreur lors du chargement du profil pour {}", username, e);
            model.addAttribute("errorMessage", "Erreur lors du chargement du profil");
        }

        return "user/profile";
    }

    /**
     * Affiche toutes les réservations de l'utilisateur avec pagination
     */
    @GetMapping("/reservations")
    public String mesReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String statut,
            Model model) {
        String username = getCurrentUsername();
        log.info("Accès aux réservations de l'utilisateur: {} - Page: {}", username, page);

        try {
            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(username);
            Pageable pageable = PageRequest.of(page, size);
            Page<ReservationDTO> reservationsPage = reservationService.getReservationsByUtilisateur(
                    user.getId(), statut, pageable);

            model.addAttribute("user", user);
            model.addAttribute("reservations", reservationsPage.getContent());
            model.addAttribute("reservationsPage", reservationsPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", reservationsPage.getTotalPages());
            model.addAttribute("filtreStatut", statut != null ? statut : "all");

            if (statut != null && !statut.isEmpty()) {
                try {
                    StatutReservation s = StatutReservation.valueOf(statut);
                    model.addAttribute("filtreLibelle", s.getLibelle());
                } catch (IllegalArgumentException e) {
                    model.addAttribute("filtreLibelle", statut);
                }
            }

            log.debug("{} réservations trouvées pour {}", reservationsPage.getTotalElements(), username);

        } catch (Exception e) {
            log.error("Erreur lors du chargement des réservations pour {}", username, e);
            model.addAttribute("errorMessage", "Erreur lors du chargement des réservations");
        }

        return "user/reservations";
    }
}
