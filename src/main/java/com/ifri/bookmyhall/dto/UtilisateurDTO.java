package com.ifri.bookmyhall.dto;

import com.ifri.bookmyhall.models.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurDTO {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 30, message = "Le nom d'utilisateur doit contenir entre 3 et 30 caractères")
    private String username;

    /**
     * Mot de passe
     */
    private String password;

    /**
     * Confirmation du mot de passe
     */
    private String confirmPassword;

    @Pattern(regexp = "^\\+[0-9]{10,15}$", message = "Numéro de téléphone invalide")
    private String telephone;

    private Role role;

    private Boolean actif;

    private String nomComplet;

    /**
     * Vérifie si les mots de passe correspondent
     */
    public boolean passwordsMatch() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    /**
     * Génère le nom complet de l'utilisateur
     */
    public String generateNomComplet() {
        if (prenom != null && nom != null) {
            return prenom + " " + nom;
        }
        return "";
    }

    /**
     * Vérifie si l'utilisateur est administrateur
     */
    public boolean isAdmin() {
        return role != null && role.equals(Role.ADMIN);
    }
}
