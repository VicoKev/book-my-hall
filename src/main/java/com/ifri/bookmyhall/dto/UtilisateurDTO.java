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
/** DTO pour le transfert des données des utilisateurs. */
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

    private String password;
    private String confirmPassword;

    @Pattern(regexp = "^\\+[0-9]{10,15}$", message = "Numéro de téléphone invalide")
    private String telephone;

    private Role role;
    private Boolean actif;
    private String nomComplet;

    /** Vérifie si le mot de passe et sa confirmation correspondent. */
    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }

    /** Génère le nom complet de l'utilisateur. */
    public String generateNomComplet() {
        return (prenom != null && nom != null) ? prenom + " " + nom : "";
    }

    /** Vérifie si l'utilisateur possède le rôle ADMIN. */
    public boolean isAdmin() {
        return role != null && role.equals(Role.ADMIN);
    }
}
