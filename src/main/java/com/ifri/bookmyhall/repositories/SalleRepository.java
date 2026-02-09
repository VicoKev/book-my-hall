package com.ifri.bookmyhall.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ifri.bookmyhall.models.Salle;

@Repository
/** Repository pour l'accès aux données des salles. */
public interface SalleRepository extends JpaRepository<Salle, Long> {

        /** Récupère une salle par son nom. */
        Optional<Salle> findByNom(String nom);

        /** Vérifie si une salle avec ce nom existe. */
        boolean existsByNom(String nom);

        /** Récupère les salles par disponibilité avec pagination. */
        Page<Salle> findByDisponible(Boolean disponible, Pageable pageable);

        /** Recherche des salles par localisation. */
        @Query("SELECT s FROM Salle s WHERE LOWER(s.localisation) LIKE LOWER(CONCAT('%', :localisation, '%'))")
        List<Salle> findByLocalisationContainingIgnoreCase(@Param("localisation") String localisation);

        /** Recherche des salles par capacité minimale. */
        @Query("SELECT s FROM Salle s WHERE s.capacite >= :capaciteMin ORDER BY s.capacite ASC")
        List<Salle> findByCapaciteGreaterThanEqual(@Param("capaciteMin") Integer capaciteMin);

        /** Recherche des salles dans une plage de prix. */
        @Query("SELECT s FROM Salle s WHERE s.prixParJour BETWEEN :prixMin AND :prixMax ORDER BY s.prixParJour ASC")
        List<Salle> findByPrixBetween(@Param("prixMin") BigDecimal prixMin, @Param("prixMax") BigDecimal prixMax);

        /** Recherche multicritère de salles avec pagination. */
        @Query("SELECT s FROM Salle s WHERE " +
                        "(:localisation IS NULL OR LOWER(s.localisation) LIKE LOWER(CONCAT('%', :localisation, '%'))) AND "
                        +
                        "(:capaciteMin IS NULL OR s.capacite >= :capaciteMin) AND " +
                        "(:prixMax IS NULL OR s.prixParJour <= :prixMax) AND " +
                        "s.disponible = :disponible " +
                        "ORDER BY s.prixParJour ASC")
        Page<Salle> searchSalles(
                        @Param("localisation") String localisation,
                        @Param("capaciteMin") Integer capaciteMin,
                        @Param("prixMax") BigDecimal prixMax,
                        @Param("disponible") Boolean disponible,
                        Pageable pageable);

        /** Compte le nombre de salles disponibles. */
        @Query("SELECT COUNT(s) FROM Salle s WHERE s.disponible = true")
        long countSallesDisponibles();

        /** Récupère les salles les plus réservées. */
        @Query("SELECT s FROM Salle s LEFT JOIN s.reservations r " +
                        "GROUP BY s.id ORDER BY COUNT(r) DESC")
        List<Salle> findSallesPopulaires();

        /** Récupère les salles sans réservation. */
        @Query("SELECT s FROM Salle s WHERE s.reservations IS EMPTY")
        List<Salle> findSallesSansReservation();
}
