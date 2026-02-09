package com.ifri.bookmyhall.controllers;

import java.util.Arrays;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.multipart.MultipartFile;

import com.ifri.bookmyhall.dto.ReservationDTO;
import com.ifri.bookmyhall.dto.SalleDTO;
import com.ifri.bookmyhall.dto.UtilisateurDTO;
import com.ifri.bookmyhall.models.Role;
import com.ifri.bookmyhall.models.Reservation.StatutReservation;
import com.ifri.bookmyhall.services.ReservationService;
import com.ifri.bookmyhall.services.SalleService;
import com.ifri.bookmyhall.services.UtilisateurService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UtilisateurService utilisateurService;
    private final SalleService salleService;
    private final ReservationService reservationService;

    /**
     * Affiche le dashboard administrateur avec statistiques
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        try {
            long totalUtilisateurs = utilisateurService.getAllUtilisateurs(PageRequest.of(0, 1)).getTotalElements();
            long totalSalles = salleService.getAllSalles(PageRequest.of(0, 1)).getTotalElements();
            long totalReservations = reservationService.getAllReservations(PageRequest.of(0, 5)).getTotalElements();
            long sallesDisponibles = salleService.countSallesDisponibles();

            model.addAttribute("totalUtilisateurs", totalUtilisateurs);
            model.addAttribute("totalSalles", totalSalles);
            model.addAttribute("totalReservations", totalReservations);
            model.addAttribute("sallesDisponibles", sallesDisponibles);

            Page<ReservationDTO> dernierePage = reservationService.getAllReservations(
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id")));
            model.addAttribute("dernieresReservations", dernierePage.getContent());

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors du chargement des statistiques");
        }

        return "admin/dashboard";
    }

    /**
     * Affiche le formulaire d'ajout d'utilisateur
     */
    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {

        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
        utilisateurDTO.setActif(true);
        model.addAttribute("utilisateurDTO", utilisateurDTO);
        model.addAttribute("roles", Arrays.asList(Role.values()));
        return "admin/user-form";
    }

    /**
     * Affiche le formulaire d'édition d'utilisateur
     */
    @GetMapping("/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        log.info("Affichage du formulaire d'édition pour l'utilisateur ID: {}", id);

        try {
            UtilisateurDTO user = utilisateurService.getUtilisateurById(id);
            model.addAttribute("utilisateurDTO", user);
            model.addAttribute("roles", Arrays.asList(Role.values()));
        } catch (Exception e) {
            log.error("Erreur lors du chargement de l'utilisateur", e);
            return "redirect:/admin/users";
        }

        return "admin/user-form";
    }

    /**
     * Crée un nouvel utilisateur
     */
    @PostMapping("/users/add")
    public String createUser(@Valid @ModelAttribute("utilisateurDTO") UtilisateurDTO utilisateurDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.info("Création d'un nouvel utilisateur: {}", utilisateurDTO.getUsername());

        if (utilisateurDTO.getPassword() == null || utilisateurDTO.getPassword().length() < 6) {
            result.rejectValue("password", "error.utilisateurDTO",
                    "Le mot de passe doit contenir au moins 6 caractères");
        }

        if (utilisateurDTO.getPassword() == null || utilisateurDTO.getConfirmPassword() == null ||
                !utilisateurDTO.getPassword().equals(utilisateurDTO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.utilisateurDTO", "Les mots de passe ne correspondent pas");
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", Arrays.asList(Role.values()));
            return "admin/user-form";
        }

        try {
            utilisateurService.createUtilisateur(utilisateurDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Utilisateur créé avec succès");
        } catch (IllegalArgumentException e) {
            log.warn("Erreur de validation lors de la création de l'utilisateur: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", Arrays.asList(Role.values()));
            return "admin/user-form";
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'utilisateur", e);
            model.addAttribute("errorMessage", "Erreur lors de la création de l'utilisateur");
            model.addAttribute("roles", Arrays.asList(Role.values()));
            return "admin/user-form";
        }

        return "redirect:/admin/users";
    }

    /**
     * Met à jour un utilisateur existant
     */
    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable Long id,
            @Valid @ModelAttribute("utilisateurDTO") UtilisateurDTO utilisateurDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.info("Mise à jour de l'utilisateur ID: {}", id);

        if (utilisateurDTO.getPassword() != null && !utilisateurDTO.getPassword().isEmpty()) {
            if (!utilisateurDTO.getPassword().equals(utilisateurDTO.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "error.utilisateurDTO", "Les mots de passe ne correspondent pas");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", Arrays.asList(Role.values()));
            return "admin/user-form";
        }

        try {
            utilisateurService.updateUtilisateur(id, utilisateurDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Utilisateur mis à jour avec succès");
        } catch (IllegalArgumentException e) {
            log.warn("Erreur de validation lors de la mise à jour de l'utilisateur: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", Arrays.asList(Role.values()));
            return "admin/user-form";
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de l'utilisateur", e);
            model.addAttribute("errorMessage", "Erreur lors de la mise à jour de l'utilisateur");
            model.addAttribute("roles", Arrays.asList(Role.values()));
            return "admin/user-form";
        }

        return "redirect:/admin/users";
    }

    /**
     * Liste tous les utilisateurs avec pagination
     */
    @GetMapping("/users")
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        log.info("Accès à la gestion des utilisateurs - Page: {}", page);

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<UtilisateurDTO> usersPage = utilisateurService.getAllUtilisateurs(pageable);

            model.addAttribute("users", usersPage.getContent());
            model.addAttribute("usersPage", usersPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usersPage.getTotalPages());

            long admins = utilisateurService.countByRole(Role.ADMIN);
            long normalUsers = utilisateurService.countByRole(Role.USER);

            model.addAttribute("totalAdmins", admins);
            model.addAttribute("totalUsers", normalUsers);

        } catch (Exception e) {
            log.error("Erreur lors du chargement des utilisateurs", e);
            model.addAttribute("errorMessage", "Erreur lors du chargement");
        }

        return "admin/users";
    }

    /**
     * Change le rôle d'un utilisateur
     */
    @PostMapping("/users/{id}/change-role")
    public String changeUserRole(@PathVariable Long id,
            @RequestParam Role newRole,
            RedirectAttributes redirectAttributes) {
        log.info("Changement de rôle pour l'utilisateur ID: {} -> {}", id, newRole);

        try {
            utilisateurService.changeRole(id, newRole);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Rôle modifié avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du changement de rôle", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    /**
     * Active ou désactive un utilisateur
     */
    @PostMapping("/users/{id}/toggle-active")
    public String toggleUserActive(@PathVariable Long id,
            @RequestParam Boolean actif,
            RedirectAttributes redirectAttributes) {
        log.info("Modification du statut actif pour l'utilisateur ID: {} -> {}", id, actif);

        try {
            utilisateurService.toggleActif(id, actif);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Statut modifié avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la modification du statut", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    /**
     * Supprime un utilisateur
     */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Suppression de l'utilisateur ID: {}", id);

        try {
            utilisateurService.deleteUtilisateur(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Utilisateur supprimé avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'utilisateur", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    /**
     * Liste toutes les salles avec pagination
     */
    @GetMapping("/salles")
    public String listSalles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        log.info("Accès à la gestion des salles - Page: {}", page);

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
            Page<SalleDTO> sallesPage = salleService.getAllSalles(pageable);
            model.addAttribute("salles", sallesPage.getContent());
            model.addAttribute("sallesPage", sallesPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", sallesPage.getTotalPages());
        } catch (Exception e) {
            log.error("Erreur lors du chargement des salles", e);
            model.addAttribute("errorMessage", "Erreur lors du chargement");
        }

        return "admin/salles";
    }

    /**
     * Affiche le formulaire de création de salle
     */
    @GetMapping("/salles/new")
    public String showCreateSalleForm(Model model) {
        model.addAttribute("salleDTO", new SalleDTO());
        model.addAttribute("isEdit", false);
        return "admin/salle-form";
    }

    /**
     * Affiche le formulaire d'édition de salle
     */
    @GetMapping("/salles/{id}/edit")
    public String showEditSalleForm(@PathVariable Long id, Model model) {
        try {
            SalleDTO salle = salleService.getSalleById(id);
            model.addAttribute("salleDTO", salle);
            model.addAttribute("isEdit", true);
        } catch (Exception e) {
            log.error("Erreur lors du chargement de la salle", e);
            return "redirect:/admin/salles";
        }
        return "admin/salle-form";
    }

    /**
     * Crée une nouvelle salle
     */
    @PostMapping("/salles/create")
    public String createSalle(@Valid @ModelAttribute("salleDTO") SalleDTO salleDTO,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.info("Création d'une nouvelle salle: {}", salleDTO.getNom());

        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/salle-form";
        }

        try {
            salleService.createSalle(salleDTO, imageFile);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Salle créée avec succès");
            return "redirect:/admin/salles";
        } catch (Exception e) {
            log.error("Erreur lors de la création de la salle", e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", false);
            return "admin/salle-form";
        }
    }

    /**
     * Met à jour une salle existante
     */
    @PostMapping("/salles/{id}/update")
    public String updateSalle(@PathVariable Long id,
            @Valid @ModelAttribute("salleDTO") SalleDTO salleDTO,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.info("Mise à jour de la salle ID: {}", id);

        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin/salle-form";
        }

        try {
            salleService.updateSalle(id, salleDTO, imageFile);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Salle mise à jour avec succès");
            return "redirect:/admin/salles";
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de la salle", e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", true);
            return "admin/salle-form";
        }
    }

    /**
     * Supprime une salle
     */
    @PostMapping("/salles/{id}/delete")
    public String deleteSalle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Suppression de la salle ID: {}", id);

        try {
            salleService.deleteSalle(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Salle supprimée avec succès");
        } catch (IllegalStateException e) {
            log.error("Impossible de supprimer la salle: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la salle", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de la suppression");
        }

        return "redirect:/admin/salles";
    }

    /**
     * Change la disponibilité d'une salle
     */
    @PostMapping("/salles/{id}/toggle-disponibilite")
    public String toggleSalleDisponibilite(@PathVariable Long id,
            @RequestParam Boolean disponible,
            RedirectAttributes redirectAttributes) {
        log.info("Modification de la disponibilité de la salle ID: {} -> {}", id, disponible);

        try {
            salleService.toggleDisponibilite(id, disponible);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Disponibilité modifiée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la modification de la disponibilité", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/salles";
    }

    /**
     * Liste toutes les réservations avec pagination
     */
    @GetMapping("/reservations")
    public String listReservations(
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        log.info("Accès à la gestion des réservations - Filtre statut: {}, Page: {}", statut, page);

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<ReservationDTO> reservationsPage;

            if (statut != null && !statut.isEmpty()) {
                reservationsPage = reservationService.getReservationsByStatut(statut, pageable);
            } else {
                reservationsPage = reservationService.getAllReservations(pageable);
            }

            model.addAttribute("reservations", reservationsPage.getContent());
            model.addAttribute("reservationsPage", reservationsPage);
            model.addAttribute("filtreStatut", statut);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", reservationsPage.getTotalPages());

            if (statut != null && !statut.isEmpty()) {
                try {
                    StatutReservation s = StatutReservation.valueOf(statut);
                    model.addAttribute("filtreLibelle", s.getLibelle());
                } catch (IllegalArgumentException e) {
                    model.addAttribute("filtreLibelle", statut);
                }
            }
        } catch (Exception e) {
            log.error("Erreur lors du chargement des réservations", e);
            model.addAttribute("errorMessage", "Erreur lors du chargement");
        }

        return "admin/reservations";
    }
}
