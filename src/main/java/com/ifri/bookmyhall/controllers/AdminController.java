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
/** Controller pour l'espace d'administration. */
public class AdminController {

    private final UtilisateurService utilisateurService;
    private final SalleService salleService;
    private final ReservationService reservationService;

    /** Affiche le tableau de bord avec les statistiques globales. */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            model.addAttribute("totalUtilisateurs",
                    utilisateurService.getAllUtilisateurs(PageRequest.of(0, 1)).getTotalElements());
            model.addAttribute("totalSalles", salleService.getAllSalles(PageRequest.of(0, 1)).getTotalElements());
            model.addAttribute("totalReservations",
                    reservationService.getAllReservations(PageRequest.of(0, 5)).getTotalElements());
            model.addAttribute("sallesDisponibles", salleService.countSallesDisponibles());

            Page<ReservationDTO> dernierePage = reservationService.getAllReservations(
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id")));
            model.addAttribute("dernieresReservations", dernierePage.getContent());

        } catch (Exception e) {
            log.error("Erreur chargement dashboard", e);
            model.addAttribute("errorMessage", "Erreur lors du chargement des statistiques");
        }
        return "admin/dashboard";
    }

    /** Affiche le formulaire d'ajout d'un utilisateur. */
    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setActif(true);
        model.addAttribute("utilisateurDTO", dto);
        model.addAttribute("roles", Arrays.asList(Role.values()));
        return "admin/user-form";
    }

    /** Affiche le formulaire de modification d'un utilisateur. */
    @GetMapping("/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("utilisateurDTO", utilisateurService.getUtilisateurById(id));
            model.addAttribute("roles", Arrays.asList(Role.values()));
        } catch (Exception e) {
            log.error("Erreur chargement utilisateur {}", id, e);
            return "redirect:/admin/users";
        }
        return "admin/user-form";
    }

    /** Traite la création d'un nouvel utilisateur par l'admin. */
    @PostMapping("/users/add")
    public String createUser(@Valid @ModelAttribute("utilisateurDTO") UtilisateurDTO dto,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            result.rejectValue("password", "error.utilisateurDTO", "Mot de passe trop court");
        }

        if (dto.getPassword() == null || !dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.utilisateurDTO", "Les mots de passe ne correspondent pas");
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", Arrays.asList(Role.values()));
            return "admin/user-form";
        }

        try {
            utilisateurService.createUtilisateur(dto);
            log.info("Utilisateur créé : {}", dto.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur créé avec succès");
        } catch (Exception e) {
            log.error("Erreur création utilisateur", e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", Arrays.asList(Role.values()));
            return "admin/user-form";
        }
        return "redirect:/admin/users";
    }

    /** Traite la mise à jour d'un utilisateur existant. */
    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("utilisateurDTO") UtilisateurDTO dto,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "error.utilisateurDTO", "Les mots de passe ne correspondent pas");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", Arrays.asList(Role.values()));
            return "admin/user-form";
        }

        try {
            utilisateurService.updateUtilisateur(id, dto);
            log.info("Utilisateur mis à jour : {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur mis à jour avec succès");
        } catch (Exception e) {
            log.error("Erreur mise à jour utilisateur {}", id, e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", Arrays.asList(Role.values()));
            return "admin/user-form";
        }
        return "redirect:/admin/users";
    }

    /** Liste les utilisateurs avec pagination. */
    @GetMapping("/users")
    public String listUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<UtilisateurDTO> usersPage = utilisateurService.getAllUtilisateurs(pageable);

            model.addAttribute("users", usersPage.getContent());
            model.addAttribute("usersPage", usersPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usersPage.getTotalPages());
            model.addAttribute("totalAdmins", utilisateurService.countByRole(Role.ADMIN));
            model.addAttribute("totalUsers", utilisateurService.countByRole(Role.USER));
        } catch (Exception e) {
            log.error("Erreur listing utilisateurs", e);
            model.addAttribute("errorMessage", "Erreur lors du chargement");
        }
        return "admin/users";
    }

    /** Modifie le rôle d'un utilisateur. */
    @PostMapping("/users/{id}/change-role")
    public String changeUserRole(@PathVariable Long id, @RequestParam Role newRole,
            RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.changeRole(id, newRole);
            log.info("Rôle utilisateur {} -> {}", id, newRole);
            redirectAttributes.addFlashAttribute("successMessage", "Rôle modifié");
        } catch (Exception e) {
            log.error("Erreur changement rôle {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /** Active ou désactive un compte utilisateur. */
    @PostMapping("/users/{id}/toggle-active")
    public String toggleUserActive(@PathVariable Long id, @RequestParam Boolean actif,
            RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.toggleActif(id, actif);
            log.info("Statut actif utilisateur {} -> {}", id, actif);
            redirectAttributes.addFlashAttribute("successMessage", "Statut modifié");
        } catch (Exception e) {
            log.error("Erreur toggle actif {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /** Supprime un utilisateur. */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.deleteUtilisateur(id);
            log.info("Utilisateur supprimé : {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé");
        } catch (Exception e) {
            log.error("Erreur suppression utilisateur {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /** Liste les salles avec pagination. */
    @GetMapping("/salles")
    public String listSalles(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
            Page<SalleDTO> sallesPage = salleService.getAllSalles(pageable);
            model.addAttribute("salles", sallesPage.getContent());
            model.addAttribute("sallesPage", sallesPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", sallesPage.getTotalPages());
        } catch (Exception e) {
            log.error("Erreur listing salles", e);
        }
        return "admin/salles";
    }

    /** Affiche le formulaire de création d'une salle. */
    @GetMapping("/salles/new")
    public String showCreateSalleForm(Model model) {
        model.addAttribute("salleDTO", new SalleDTO());
        model.addAttribute("isEdit", false);
        return "admin/salle-form";
    }

    /** Affiche le formulaire de modification d'une salle. */
    @GetMapping("/salles/{id}/edit")
    public String showEditSalleForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("salleDTO", salleService.getSalleById(id));
            model.addAttribute("isEdit", true);
        } catch (Exception e) {
            log.error("Erreur chargement salle {}", id, e);
            return "redirect:/admin/salles";
        }
        return "admin/salle-form";
    }

    /** Traite la création d'une nouvelle salle. */
    @PostMapping("/salles/create")
    public String createSalle(@Valid @ModelAttribute("salleDTO") SalleDTO dto,
            @RequestParam(value = "imageFile", required = false) MultipartFile file,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/salle-form";
        }
        try {
            salleService.createSalle(dto, file);
            log.info("Salle créée : {}", dto.getNom());
            redirectAttributes.addFlashAttribute("successMessage", "Salle créée");
            return "redirect:/admin/salles";
        } catch (Exception e) {
            log.error("Erreur création salle", e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", false);
            return "admin/salle-form";
        }
    }

    /** Traite la mise à jour d'une salle existante. */
    @PostMapping("/salles/{id}/update")
    public String updateSalle(@PathVariable Long id, @Valid @ModelAttribute("salleDTO") SalleDTO dto,
            @RequestParam(value = "imageFile", required = false) MultipartFile file,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin/salle-form";
        }
        try {
            salleService.updateSalle(id, dto, file);
            log.info("Salle mise à jour : {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Salle mise à jour");
            return "redirect:/admin/salles";
        } catch (Exception e) {
            log.error("Erreur mise à jour salle {}", id, e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", true);
            return "admin/salle-form";
        }
    }

    /** Supprime une salle. */
    @PostMapping("/salles/{id}/delete")
    public String deleteSalle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            salleService.deleteSalle(id);
            log.info("Salle supprimée : {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Salle supprimée");
        } catch (Exception e) {
            log.error("Erreur suppression salle {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/salles";
    }

    /** Modifie la disponibilité d'une salle. */
    @PostMapping("/salles/{id}/toggle-disponibilite")
    public String toggleSalleDisponibilite(@PathVariable Long id, @RequestParam Boolean disponible,
            RedirectAttributes redirectAttributes) {
        try {
            salleService.toggleDisponibilite(id, disponible);
            log.info("Dispo salle {} -> {}", id, disponible);
            redirectAttributes.addFlashAttribute("successMessage", "Disponibilité modifiée");
        } catch (Exception e) {
            log.error("Erreur toggle dispo salle {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/salles";
    }

    /** Liste les réservations avec filtrage par statut. */
    @GetMapping("/reservations")
    public String listReservations(@RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<ReservationDTO> reservationsPage = (statut != null && !statut.isEmpty())
                    ? reservationService.getReservationsByStatut(statut, pageable)
                    : reservationService.getAllReservations(pageable);

            model.addAttribute("reservations", reservationsPage.getContent());
            model.addAttribute("reservationsPage", reservationsPage);
            model.addAttribute("filtreStatut", statut);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", reservationsPage.getTotalPages());

            if (statut != null && !statut.isEmpty()) {
                try {
                    model.addAttribute("filtreLibelle", StatutReservation.valueOf(statut).getLibelle());
                } catch (Exception e) {
                    model.addAttribute("filtreLibelle", statut);
                }
            }
        } catch (Exception e) {
            log.error("Erreur listing réservations", e);
        }
        return "admin/reservations";
    }
}
