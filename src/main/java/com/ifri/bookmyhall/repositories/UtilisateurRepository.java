package com.ifri.bookmyhall.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ifri.bookmyhall.models.Role;
import com.ifri.bookmyhall.models.Utilisateur;

@Repository
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
     * Trouve tous les utilisateurs par rôle avec pagination
     */
    Page<Utilisateur> findByRole(Role role, Pageable pageable);

    /**
     * Trouve tous les utilisateurs actifs ou inactifs
     */
    List<Utilisateur> findByActif(Boolean actif);

    /**
     * Recherche des utilisateurs par nom ou prénom avec pagination
     */
    @Query("SELECT u FROM Utilisateur u WHERE " +
            "LOWER(u.nom) LIKE LOWER(CONCAT('%', :nom, '%')) OR " +
            "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))")
    Page<Utilisateur> searchByNomOrPrenom(@Param("nom") String nom, @Param("prenom") String prenom, Pageable pageable);

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
