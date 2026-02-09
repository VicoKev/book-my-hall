package com.ifri.bookmyhall.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/** Entité représentant une réservation de salle. */
public class Reservation {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être future")
    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = true)
    private LocalDate dateFin;

    @NotNull(message = "L'heure de début est obligatoire")
    @Column(nullable = false)
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    @Column(nullable = false)
    private LocalTime heureFin;

    @NotBlank(message = "Le type d'événement est obligatoire")
    @Size(max = 100, message = "Le type d'événement ne peut pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String typeEvenement;

    @Column(length = 500)
    private String description;

    @NotNull(message = "Le nombre de personnes est obligatoire")
    @Min(value = 1, message = "Le nombre de personnes doit être au moins 1")
    @Column(nullable = false)
    private Integer nombrePersonnes;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutReservation statut = StatutReservation.PENDING;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salle_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Salle salle;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** Énumération des statuts possibles d'une réservation. */
    public enum StatutReservation {
        PENDING("En attente"),
        CONFIRMED("Confirmée"),
        CANCELLED("Annulée"),
        COMPLETED("Terminée");

        private final String libelle;

        StatutReservation(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    /** Valide la cohérence temporelle de la réservation. */
    @PrePersist
    @PreUpdate
    private void validateHoraires() {
        if (heureDebut != null && heureFin != null && (heureFin.isBefore(heureDebut) || heureFin.equals(heureDebut))) {
            throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
        }
        if (dateFin != null && dateDebut != null && dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin doit être après ou égale à la date de début");
        }
    }

    /** Calcule la durée en heures (pour une même journée). */
    public long getDureeEnHeures() {
        return (heureDebut != null && heureFin != null) ? java.time.Duration.between(heureDebut, heureFin).toHours()
                : 0;
    }

    /** Calcule le nombre total de jours réservés. */
    public long getNombreDeJours() {
        return (dateFin == null) ? 1 : java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
    }

    /** Vérifie si la réservation peut être annulée. */
    public boolean estAnnulable() {
        return statut == StatutReservation.PENDING || statut == StatutReservation.CONFIRMED;
    }

    /** Annule la réservation. */
    public void annulerR() {
        if (!estAnnulable())
            throw new IllegalStateException("Impossible d'annuler");
        this.statut = StatutReservation.CANCELLED;
    }

    /** Confirme la réservation. */
    public void confirmerR() {
        if (statut != StatutReservation.PENDING)
            throw new IllegalStateException("Doit être en attente");
        this.statut = StatutReservation.CONFIRMED;
    }
}
