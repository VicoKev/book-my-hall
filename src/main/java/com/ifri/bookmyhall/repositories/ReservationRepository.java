package com.ifri.bookmyhall.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ifri.bookmyhall.models.Reservation;
import com.ifri.bookmyhall.models.Reservation.StatutReservation;
import com.ifri.bookmyhall.models.Salle;
import com.ifri.bookmyhall.models.Utilisateur;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Trouve toutes les réservations d'un utilisateur
     */
    List<Reservation> findByUtilisateur(Utilisateur utilisateur);

    /**
     * Trouve toutes les réservations d'une salle
     */
    List<Reservation> findBySalle(Salle salle);

    /**
     * Trouve les réservations par statut
     */
    List<Reservation> findByStatut(StatutReservation statut);

    /**
     * Trouve les réservations d'un utilisateur triées par date décroissante
     */
    @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :utilisateurId " +
           "ORDER BY r.dateReservation DESC, r.heureDebut DESC")
    List<Reservation> findByUtilisateurIdOrderByDateDesc(@Param("utilisateurId") Long utilisateurId);

    /**
     * Trouve les réservations d'une salle pour une date donnée
     */
    @Query("SELECT r FROM Reservation r WHERE r.salle.id = :salleId " +
           "AND r.dateReservation = :date ORDER BY r.heureDebut ASC")
    List<Reservation> findBySalleIdAndDate(@Param("salleId") Long salleId, @Param("date") LocalDate date);

    /**
     * Vérifie s'il existe un conflit de réservation
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
           "WHERE r.salle.id = :salleId " +
           "AND r.dateReservation = :date " +
           "AND r.statut NOT IN ('CANCELLED') " +
           "AND ((r.heureDebut < :heureFin AND r.heureFin > :heureDebut))")
    boolean existsConflictingReservation(
        @Param("salleId") Long salleId,
        @Param("date") LocalDate date,
        @Param("heureDebut") LocalTime heureDebut,
        @Param("heureFin") LocalTime heureFin
    );

    /**
     * Vérifie un conflit en excluant une réservation spécifique pour les mises à jour)
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
           "WHERE r.id != :reservationId " +
           "AND r.salle.id = :salleId " +
           "AND r.dateReservation = :date " +
           "AND r.statut NOT IN ('CANCELLED') " +
           "AND ((r.heureDebut < :heureFin AND r.heureFin > :heureDebut))")
    boolean existsConflictingReservationExcludingId(
        @Param("reservationId") Long reservationId,
        @Param("salleId") Long salleId,
        @Param("date") LocalDate date,
        @Param("heureDebut") LocalTime heureDebut,
        @Param("heureFin") LocalTime heureFin
    );

    /**
     * Trouve les réservations futures d'un utilisateur
     */
    @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :utilisateurId " +
           "AND r.dateReservation >= :dateActuelle " +
           "ORDER BY r.dateReservation ASC, r.heureDebut ASC")
    List<Reservation> findFutureReservationsByUtilisateur(
        @Param("utilisateurId") Long utilisateurId,
        @Param("dateActuelle") LocalDate dateActuelle
    );

    /**
     * Trouve les réservations passées d'un utilisateur
     */
    @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :utilisateurId " +
           "AND r.dateReservation < :dateActuelle " +
           "ORDER BY r.dateReservation DESC, r.heureDebut DESC")
    List<Reservation> findPastReservationsByUtilisateur(
        @Param("utilisateurId") Long utilisateurId,
        @Param("dateActuelle") LocalDate dateActuelle
    );

    /**
     * Trouve toutes les réservations entre deux dates
     */
    @Query("SELECT r FROM Reservation r WHERE r.dateReservation BETWEEN :dateDebut AND :dateFin " +
           "ORDER BY r.dateReservation ASC, r.heureDebut ASC")
    List<Reservation> findReservationsBetweenDates(
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
    );

    /**
     * Compte les réservations d'un utilisateur
     */
    long countByUtilisateurId(Long utilisateurId);

    /**
     * Compte les réservations d'une salle
     */
    long countBySalleId(Long salleId);
}
