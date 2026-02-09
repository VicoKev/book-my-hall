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
/** DTO pour le transfert des données des réservations. */
public class ReservationDTO {

    private Long id;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;

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

    @NotNull(message = "L'utilisateur est obligatoire")
    private Long utilisateurId;

    private String utilisateurNom;

    @NotNull(message = "La salle est obligatoire")
    private Long salleId;

    private String salleNom;

    private Integer salleCapacite;

    /** Vérifie la validité du créneau horaire. */
    public boolean hasValidTimeRange() {
        return heureDebut != null && heureFin != null && heureFin.isAfter(heureDebut);
    }

    /** Vérifie si la capacité de la salle est respectée. */
    public boolean isWithinCapacity() {
        return nombrePersonnes == null || salleCapacite == null || nombrePersonnes <= salleCapacite;
    }

    /** Calcule la durée de l'événement en heures. */
    public long getDureeEnHeures() {
        return (heureDebut != null && heureFin != null) ? java.time.Duration.between(heureDebut, heureFin).toHours()
                : 0;
    }

    /** Vérifie la validité de la plage de dates. */
    public boolean hasValidDateRange() {
        return dateFin == null || (dateDebut != null && !dateFin.isBefore(dateDebut));
    }

    /** Calcule le nombre total de jours de réservation. */
    public long getNombreDeJours() {
        if (dateFin == null)
            return 1;
        if (dateDebut == null)
            return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
    }
}
