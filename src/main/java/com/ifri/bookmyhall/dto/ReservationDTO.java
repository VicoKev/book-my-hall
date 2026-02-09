package com.ifri.bookmyhall.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.ifri.bookmyhall.models.Reservation.StatutReservation;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private Long id;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;

    /**
     * Date de fin de la réservation (optionnel pour réservations multi-jours)
     * Si null, la réservation est pour un seul jour
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;

    @NotNull(message = "L'heure de début est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureFin;

    @NotBlank(message = "Le type d'événement est obligatoire")
    @Size(max = 100, message = "Le type d'événement ne peut pas dépasser 100 caractères")
    private String typeEvenement;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @NotNull(message = "Le nombre de personnes est obligatoire")
    @Min(value = 1, message = "Le nombre de personnes doit être au moins 1")
    private Integer nombrePersonnes;

    private BigDecimal montantTotal;

    private StatutReservation statut;

    /**
     * ID de l'utilisateur qui réserve
     */
    @NotNull(message = "L'utilisateur est obligatoire")
    private Long utilisateurId;

    /**
     * Nom complet de l'utilisateur
     */
    private String utilisateurNom;

    /**
     * ID de la salle réservée
     */
    @NotNull(message = "La salle est obligatoire")
    private Long salleId;

    /**
     * Nom de la salle
     */
    private String salleNom;

    /**
     * Capacité de la salle
     */
    private Integer salleCapacite;

    /**
     * Vérifie que l'heure de fin est après l'heure de début
     */
    public boolean hasValidTimeRange() {
        if (heureDebut == null || heureFin == null) {
            return false;
        }
        return heureFin.isAfter(heureDebut);
    }

    /**
     * Vérifie que le nombre de personnes ne dépasse pas la capacité de la salle
     */
    public boolean isWithinCapacity() {
        if (nombrePersonnes == null || salleCapacite == null) {
            return true;
        }
        return nombrePersonnes <= salleCapacite;
    }

    /**
     * Calcule la durée en heures
     */
    public long getDureeEnHeures() {
        if (heureDebut != null && heureFin != null) {
            return java.time.Duration.between(heureDebut, heureFin).toHours();
        }
        return 0;
    }

    /**
     * Vérifie que la plage de dates est valide
     * (dateFin >= dateDebut ou dateFin est null)
     */
    public boolean hasValidDateRange() {
        if (dateFin == null) {
            return true; // Réservation d'un seul jour
        }
        if (dateDebut == null) {
            return false;
        }
        return !dateFin.isBefore(dateDebut);
    }

    /**
     * Calcule le nombre de jours de la réservation
     * Si dateFin est null, retourne 1 (réservation d'un seul jour)
     */
    public long getNombreDeJours() {
        if (dateFin == null) {
            return 1;
        }
        if (dateDebut == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
    }
}
