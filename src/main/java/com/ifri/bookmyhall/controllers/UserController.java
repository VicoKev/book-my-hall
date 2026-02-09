package com.ifri.bookmyhall.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
/** Controller pour l'espace utilisateur (dashboard, profil). */
public class UserController {

    private final UtilisateurService utilisateurService;
    private final ReservationService reservationService;

    /** Récupère le nom d'utilisateur de la session courante. */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    /**
     * Affiche le tableau de bord de l'utilisateur avec ses réservations récentes.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String username = getCurrentUsername();
        try {
            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(username);
            model.addAttribute("user", user);

            Page<ReservationDTO> reservationsPage = reservationService.getReservationsByUtilisateur(user.getId(),
                    PageRequest.of(0, 5));
            model.addAttribute("reservations", reservationsPage.getContent());

            Page<ReservationDTO> reservationsFuturesPage = reservationService
                    .getFutureReservationsByUtilisateur(user.getId(), PageRequest.of(0, 5));
            model.addAttribute("reservationsFutures", reservationsFuturesPage.getContent());

            model.addAttribute("totalReservations", reservationsPage.getTotalElements());
            model.addAttribute("reservationsEnCours", reservationsPage.getContent().stream()
                    .filter(r -> r.getStatut() != null && r.getStatut() != StatutReservation.CANCELLED
                            && r.getStatut() != StatutReservation.COMPLETED)
                    .count());

        } catch (Exception e) {
            log.error("Erreur dashboard utilisateur {}", username, e);
            model.addAttribute("errorMessage", "Erreur lors du chargement");
        }
        return "user/dashboard";
    }

    /** Affiche le profil de l'utilisateur. */
    @GetMapping("/profile")
    public String profile(Model model) {
        String username = getCurrentUsername();
        try {
            model.addAttribute("user", utilisateurService.getUtilisateurByUsername(username));
        } catch (Exception e) {
            log.error("Erreur profil utilisateur {}", username, e);
        }
        return "user/profile";
    }

    /**
     * Liste les réservations de l'utilisateur connecté avec filtrage et pagination.
     */
    @GetMapping("/reservations")
    public String mesReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String statut,
            Model model) {
        String username = getCurrentUsername();
        try {
            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(username);
            Page<ReservationDTO> resPage = reservationService.getReservationsByUtilisateur(user.getId(), statut,
                    PageRequest.of(page, size));

            model.addAttribute("user", user);
            model.addAttribute("reservations", resPage.getContent());
            model.addAttribute("reservationsPage", resPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", resPage.getTotalPages());
            model.addAttribute("filtreStatut", statut != null ? statut : "all");

            if (statut != null && !statut.isEmpty()) {
                try {
                    model.addAttribute("filtreLibelle", StatutReservation.valueOf(statut).getLibelle());
                } catch (Exception e) {
                    model.addAttribute("filtreLibelle", statut);
                }
            }
        } catch (Exception e) {
            log.error("Erreur reservations utilisateur {}", username, e);
        }
        return "user/reservations";
    }
}
