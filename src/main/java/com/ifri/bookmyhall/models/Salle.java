package com.ifri.bookmyhall.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
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
@Table(name = "salles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salle {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Le nom de la salle est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 10, message = "La capacité minimale est de 10 personnes")
    @Max(value = 1000, message = "La capacité maximale est de 1000 personnes")
    @Column(nullable = false)
    private Integer capacite;

    @NotBlank(message = "La localisation est obligatoire")
    @Size(min = 5, max = 200, message = "La localisation doit contenir entre 5 et 200 caractères")
    @Column(nullable = false, length = 200)
    private String localisation;

    @Column(length = 500)
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixParJour;

    @Column(length = 255)
    private String imageUrl;

    @Column(length = 500)
    private String equipements;

    @Column(nullable = false)
    @Builder.Default
    private Boolean disponible = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Liste des réservations de cette salle
     */
    @OneToMany(mappedBy = "salle", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reservation> reservations = new ArrayList<>();

    /**
     * Active ou désactive la disponibilité de la salle
     */
    public void setDisponibilite(boolean status) {
        this.disponible = status;
    }
}
