package com.ifri.bookmyhall.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
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
public class SalleDTO {

    private Long id;

    @NotBlank(message = "Le nom de la salle est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String nom;

    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 10, message = "La capacité minimale est de 10 personnes")
    @Max(value = 1000, message = "La capacité maximale est de 1000 personnes")
    private Integer capacite;

    @NotBlank(message = "La localisation est obligatoire")
    @Size(min = 5, max = 200, message = "La localisation doit contenir entre 5 et 200 caractères")
    private String localisation;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal prixParJour;

    private String imageFileName;

    @Size(max = 500, message = "Les équipements ne peuvent pas dépasser 500 caractères")
    private String equipements;

    private Boolean disponible;

    private Long nombreReservations;

    /**
     * Vérifie si la salle est réservable
     */
    public boolean isReservable() {
        return disponible != null && disponible;
    }
}
