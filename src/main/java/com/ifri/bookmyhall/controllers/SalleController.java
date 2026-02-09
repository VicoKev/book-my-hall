package com.ifri.bookmyhall.controllers;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class SalleController {

    private final SalleService salleService;

    /**
     * Affiche la liste de toutes les salles disponibles
     * Avec possibilité de recherche et filtrage
     */
    @GetMapping
    public String listSalles(
            @RequestParam(required = false) String localisation,
            @RequestParam(required = false) Integer capaciteMin,
            @RequestParam(required = false) BigDecimal prixMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        log.info("Affichage de la liste des salles - Filtres: localisation={}, capaciteMin={}, prixMax={}, Page: {}",
                localisation, capaciteMin, prixMax, page);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SalleDTO> sallesPage;

            if (localisation != null || capaciteMin != null || prixMax != null) {
                sallesPage = salleService.searchSalles(localisation, capaciteMin, prixMax, pageable);
                model.addAttribute("hasFilters", true);
                log.debug("{} salles trouvées avec les filtres", sallesPage.getTotalElements());
            } else {
                sallesPage = salleService.getSallesDisponibles(pageable);
                model.addAttribute("hasFilters", false);
                log.debug("{} salles disponibles", sallesPage.getTotalElements());
            }

            model.addAttribute("salles", sallesPage.getContent());
            model.addAttribute("sallesPage", sallesPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", sallesPage.getTotalPages());

            model.addAttribute("localisation", localisation);
            model.addAttribute("capaciteMin", capaciteMin);
            model.addAttribute("prixMax", prixMax);

        } catch (Exception e) {
            log.error("Erreur lors du chargement des salles", e);
            model.addAttribute("errorMessage", "Erreur lors du chargement des salles");
        }

        return "salles/list";
    }

    /**
     * Affiche les détails d'une salle spécifique
     */
    @GetMapping("/{id}")
    public String detailsSalle(@PathVariable Long id, Model model) {
        log.info("Affichage des détails de la salle ID: {}", id);

        try {
            SalleDTO salle = salleService.getSalleById(id);
            model.addAttribute("salle", salle);

            log.debug("Salle trouvée: {}", salle.getNom());

        } catch (Exception e) {
            log.error("Erreur lors du chargement de la salle ID: {}", id, e);
            model.addAttribute("errorMessage", "Salle non trouvée");
            return "redirect:/salles";
        }

        return "salles/details";
    }

    /**
     * Redirige vers le formulaire de réservation pour une salle
     */
    @GetMapping("/{id}/reserver")
    public String reserverSalle(@PathVariable Long id) {
        log.info("Redirection vers réservation pour la salle ID: {}", id);
        return "redirect:/reservations/new?salleId=" + id;
    }
}
