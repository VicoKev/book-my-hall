package com.ifri.bookmyhall.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ifri.bookmyhall.dto.ReservationDTO;
import com.ifri.bookmyhall.dto.SalleDTO;
import com.ifri.bookmyhall.dto.UtilisateurDTO;
import com.ifri.bookmyhall.models.Reservation.StatutReservation;
import com.ifri.bookmyhall.services.ReservationService;
import com.ifri.bookmyhall.services.SalleService;
import com.ifri.bookmyhall.services.UtilisateurService;

import jakarta.validation.Valid;
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
    private final SalleService salleService;

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

    /**
     * Liste les salles pour l'utilisateur connecté.
     */
    @GetMapping("/salles")
    public String listSalles(
            @RequestParam(required = false) String localisation,
            @RequestParam(required = false) Integer capaciteMin,
            @RequestParam(required = false) BigDecimal prixMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SalleDTO> sallesPage;

            if (localisation != null || capaciteMin != null || prixMax != null) {
                sallesPage = salleService.searchSalles(localisation, capaciteMin, prixMax, pageable);
                model.addAttribute("hasFilters", true);
            } else {
                sallesPage = salleService.getSallesDisponibles(pageable);
                model.addAttribute("hasFilters", false);
            }

            model.addAttribute("salles", sallesPage.getContent());
            model.addAttribute("sallesPage", sallesPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", sallesPage.getTotalPages());
            model.addAttribute("localisation", localisation);
            model.addAttribute("capaciteMin", capaciteMin);
            model.addAttribute("prixMax", prixMax);

        } catch (Exception e) {
            log.error("Erreur chargement salles pour utilisateur", e);
            model.addAttribute("errorMessage", "Erreur lors du chargement");
        }
        return "user/salle-list";
    }

    /**
     * Affiche les détails d'une salle pour l'utilisateur connecté.
     */
    @GetMapping("/salles/{id}")
    public String detailsSalle(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("salle", salleService.getSalleById(id));
        } catch (Exception e) {
            log.error("Erreur chargement salle {} pour utilisateur", id, e);
            model.addAttribute("errorMessage", "Salle non trouvée");
            return "redirect:/user/salles";
        }
        return "user/salle-details";
    }

    /** Affiche le formulaire de nouvelle réservation pour l'utilisateur. */
    @GetMapping("/reservations/new")
    public String showReservationForm(@RequestParam Long salleId, Model model) {
        try {
            SalleDTO salle = salleService.getSalleById(salleId);
            if (!salle.getDisponible()) {
                model.addAttribute("errorMessage", "Salle indisponible");
                return "redirect:/user/salles/" + salleId;
            }

            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(getCurrentUsername());
            model.addAttribute("reservationDTO", ReservationDTO.builder()
                    .salleId(salleId).utilisateurId(user.getId())
                    .dateDebut(LocalDate.now().plusDays(1)).build());
            model.addAttribute("salle", salle);
            model.addAttribute("user", user);

        } catch (Exception e) {
            log.error("Erreur chargement formulaire réservation pour utilisateur", e);
            return "redirect:/user/salles";
        }
        return "user/reservation-form";
    }

    /** Traite la soumission d'une nouvelle réservation par l'utilisateur. */
    @PostMapping("/reservations/create")
    public String createReservation(@Valid @ModelAttribute("reservationDTO") ReservationDTO dto,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            try {
                model.addAttribute("salle", salleService.getSalleById(dto.getSalleId()));
                model.addAttribute("user", utilisateurService.getUtilisateurByUsername(getCurrentUsername()));
            } catch (Exception e) {
            }
            return "user/reservation-form";
        }

        if (!dto.hasValidTimeRange()) {
            model.addAttribute("errorMessage", "Plage horaire invalide");
            try {
                model.addAttribute("salle", salleService.getSalleById(dto.getSalleId()));
                model.addAttribute("user", utilisateurService.getUtilisateurByUsername(getCurrentUsername()));
            } catch (Exception e) {
            }
            return "user/reservation-form";
        }

        try {
            ReservationDTO created = reservationService.createReservation(dto);
            log.info("Réservation utilisateur créée : {}", created.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Réservation créée avec succès !");
            return "redirect:/user/reservations/" + created.getId();
        } catch (Exception e) {
            log.error("Erreur création réservation utilisateur", e);
            model.addAttribute("errorMessage", e.getMessage());
            try {
                model.addAttribute("salle", salleService.getSalleById(dto.getSalleId()));
                model.addAttribute("user", utilisateurService.getUtilisateurByUsername(getCurrentUsername()));
            } catch (Exception ex) {
            }
            return "user/reservation-form";
        }
    }

    /** Affiche les détails d'une réservation pour l'utilisateur. */
    @GetMapping("/reservations/{id}")
    public String detailsReservation(@PathVariable Long id, Model model) {
        String username = getCurrentUsername();
        try {
            ReservationDTO res = reservationService.getReservationById(id);
            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(username);

            if (!res.getUtilisateurId().equals(user.getId())) {
                log.warn("Tentative d'accès non autorisé à la réservation {} par {}", id, username);
                return "redirect:/user/reservations";
            }
            model.addAttribute("reservation", res);
            model.addAttribute("currentUser", user);
        } catch (Exception e) {
            log.error("Erreur chargement détails réservation {} pour utilisateur", id, e);
            return "redirect:/user/reservations";
        }
        return "user/reservation-details";
    }

    /** Permet à l'utilisateur d'annuler une de ses réservations. */
    @PostMapping("/reservations/{id}/annuler")
    public String annulerReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String username = getCurrentUsername();
        try {
            ReservationDTO res = reservationService.getReservationById(id);
            UtilisateurDTO user = utilisateurService.getUtilisateurByUsername(username);

            if (!res.getUtilisateurId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Action non autorisée");
                return "redirect:/user/reservations";
            }
            reservationService.annulerReservation(id);
            log.info("Réservation {} annulée par l'utilisateur {}", id, username);
            redirectAttributes.addFlashAttribute("successMessage", "Votre réservation a été annulée");
        } catch (Exception e) {
            log.error("Erreur annulation réservation {} par utilisateur", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/reservations/" + id;
    }
}
