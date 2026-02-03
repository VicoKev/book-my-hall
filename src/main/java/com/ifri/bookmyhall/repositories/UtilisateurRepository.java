package com.ifri.bookmyhall.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ifri.bookmyhall.models.Role;
import com.ifri.bookmyhall.models.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Trouve un utilisateur par son nom d'utilisateur
     */
    Optional<Utilisateur> findByUsername(String username);

    /**
     * Trouve un utilisateur par son email
     */
    Optional<Utilisateur> findByEmail(String email);
    
    /**
     * Vérifie si un nom d'utilisateur existe déjà
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);
    
    /**
     * Trouve tous les utilisateurs par rôle
     */
    List<Utilisateur> findByRole(Role role);

    /**
     * Trouve tous les utilisateurs actifs ou inactifs
     */
    List<Utilisateur> findByActif(Boolean actif);
    
    /**
     * Recherche des utilisateurs par nom ou prénom (insensible à la casse)
     */
    @Query("SELECT u FROM Utilisateur u WHERE " +
           "LOWER(u.nom) LIKE LOWER(CONCAT('%', :nom, '%')) OR " +
           "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))")
    List<Utilisateur> searchByNomOrPrenom(@Param("nom") String nom, @Param("prenom") String prenom);

    /**
     * Compte le nombre d'utilisateurs par rôle
     */
    long countByRole(Role role);

    /**
     * Trouve les utilisateurs avec au moins une réservation
     */
    @Query("SELECT DISTINCT u FROM Utilisateur u JOIN u.reservations r")
    List<Utilisateur> findUtilisateursWithReservations();
}
