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
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "reservations", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"salle_id", "date_reservation", "heure_debut"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "La date de réservation est obligatoire")
    @Future(message = "La date de réservation doit être future")
    @Column(nullable = false)
    private LocalDate dateReservation;

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

    /**
     * Statut de la réservation
     * PENDING : en attente de confirmation
     * CONFIRMED : confirmée
     * CANCELLED : annulée
     * COMPLETED : terminée
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutReservation statut = StatutReservation.PENDING;

    /**
     * Utilisateur ayant effectué la réservation
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Utilisateur utilisateur;

    /**
     * Salle réservée
     */
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

    /**
     * Énumération des statuts possibles d'une réservation
     */
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

    /**
     * Vérifie que l'heure de fin est après l'heure de début
     */
    @PrePersist
    @PreUpdate
    private void validateHoraires() {
        if (heureDebut != null && heureFin != null) {
            if (heureFin.isBefore(heureDebut) || heureFin.equals(heureDebut)) {
                throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
            }
        }
    }

    /**
     * Calcule la durée de la réservation en heures
     */
    public long getDureeEnHeures() {
        if (heureDebut != null && heureFin != null) {
            return java.time.Duration.between(heureDebut, heureFin).toHours();
        }
        return 0;
    }

    /**
     * Vérifie si la réservation peut être annulée
     */
    public boolean estAnnulable() {
        return statut == StatutReservation.PENDING || statut == StatutReservation.CONFIRMED;
    }

    /**
     * Annule la réservation
     */
    public void annulerR() {
        if (estAnnulable()) {
            this.statut = StatutReservation.CANCELLED;
        } else {
            throw new IllegalStateException("Cette réservation ne peut pas être annulée");
        }
    }

    /**
     * Confirme la réservation
     */
    public void confirmerR() {
        if (statut == StatutReservation.PENDING) {
            this.statut = StatutReservation.CONFIRMED;
        } else {
            throw new IllegalStateException("Seules les réservations en attente peuvent être confirmées");
        }
    }
}
