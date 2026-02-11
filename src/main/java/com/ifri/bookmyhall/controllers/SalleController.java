package com.ifri.bookmyhall.controllers;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ifri.bookmyhall.dto.SalleDTO;
import com.ifri.bookmyhall.services.SalleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/salles")
@RequiredArgsConstructor
@Slf4j
/** Controller pour la gestion des salles côté utilisateur. */
public class SalleController {

    private final SalleService salleService;

    /** Affiche la liste des salles avec filtrage et pagination. */
    @GetMapping
    public String listSalles(
            @RequestParam(required = false) String localisation,
            @RequestParam(required = false) Integer capaciteMin,
            @RequestParam(required = false) BigDecimal prixMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/user/salles";
        }

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
            log.error("Erreur chargement salles", e);
            model.addAttribute("errorMessage", "Erreur lors du chargement");
        }
        return "salles/list";
    }

    /** Affiche les détails d'une salle spécifique. */
    @GetMapping("/{id}")
    public String detailsSalle(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/user/salles/" + id;
        }
        try {
            model.addAttribute("salle", salleService.getSalleById(id));
        } catch (Exception e) {
            log.error("Erreur chargement salle {}", id, e);
            model.addAttribute("errorMessage", "Salle non trouvée");
            return "redirect:/salles";
        }
        return "salles/details";
    }

    /** Redirige vers le formulaire de réservation pour une salle. */
    @GetMapping("/{id}/reserver")
    public String reserverSalle(@PathVariable Long id) {
        return "redirect:/user/reservations/new?salleId=" + id;
    }
}
