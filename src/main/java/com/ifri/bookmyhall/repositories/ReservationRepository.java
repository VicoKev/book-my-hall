package com.ifri.bookmyhall.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        * Trouve les réservations par statut avec pagination
        */
       Page<Reservation> findByStatut(StatutReservation statut, Pageable pageable);

       /**
        * Trouve les réservations d'un utilisateur triées par date décroissante avec
        * pagination
        */
       @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :utilisateurId " +
                     "ORDER BY r.dateDebut DESC, r.heureDebut DESC")
       Page<Reservation> findByUtilisateurIdOrderByDateDesc(@Param("utilisateurId") Long utilisateurId,
                     Pageable pageable);

       /**
        * Trouve les réservations d'un utilisateur par statut avec pagination
        */
       @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :utilisateurId AND r.statut = :statut " +
                     "ORDER BY r.dateDebut DESC, r.heureDebut DESC")
       Page<Reservation> findByUtilisateurIdAndStatut(@Param("utilisateurId") Long utilisateurId,
                     @Param("statut") StatutReservation statut,
                     Pageable pageable);

       /**
        * Trouve les réservations d'une salle pour une date donnée
        */
       @Query("SELECT r FROM Reservation r WHERE r.salle.id = :salleId " +
                     "AND ((r.dateFin IS NULL AND r.dateDebut = :date) OR " +
                     "(r.dateFin IS NOT NULL AND r.dateDebut <= :date AND r.dateFin >= :date)) " +
                     "ORDER BY r.heureDebut ASC")
       List<Reservation> findBySalleIdAndDate(@Param("salleId") Long salleId, @Param("date") LocalDate date);

       /**
        * Vérifie s'il existe un conflit de réservation pour une plage de dates
        * Deux réservations se chevauchent si leurs plages de dates ET leurs horaires
        * se chevauchent
        */
       @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
                     "WHERE r.salle.id = :salleId " +
                     "AND r.statut NOT IN ('CANCELLED') " +
                     "AND ((r.dateFin IS NULL AND r.dateDebut >= :dateDebut AND r.dateDebut <= :dateFin) OR " +
                     "(r.dateFin IS NOT NULL AND r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut)) " +
                     "AND ((r.heureDebut < :heureFin AND r.heureFin > :heureDebut))")
       boolean existsConflictingReservation(
                     @Param("salleId") Long salleId,
                     @Param("dateDebut") LocalDate dateDebut,
                     @Param("dateFin") LocalDate dateFin,
                     @Param("heureDebut") LocalTime heureDebut,
                     @Param("heureFin") LocalTime heureFin);

       /**
        * Vérifie un conflit en excluant une réservation spécifique (pour les mises à
        * jour)
        */
       @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
                     "WHERE r.id != :reservationId " +
                     "AND r.salle.id = :salleId " +
                     "AND r.statut NOT IN ('CANCELLED') " +
                     "AND ((r.dateFin IS NULL AND r.dateDebut >= :dateDebut AND r.dateDebut <= :dateFin) OR " +
                     "(r.dateFin IS NOT NULL AND r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut)) " +
                     "AND ((r.heureDebut < :heureFin AND r.heureFin > :heureDebut))")
       boolean existsConflictingReservationExcludingId(
                     @Param("reservationId") Long reservationId,
                     @Param("salleId") Long salleId,
                     @Param("dateDebut") LocalDate dateDebut,
                     @Param("dateFin") LocalDate dateFin,
                     @Param("heureDebut") LocalTime heureDebut,
                     @Param("heureFin") LocalTime heureFin);

       /**
        * Trouve les réservations futures d'un utilisateur avec pagination
        */
       @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :utilisateurId " +
                     "AND ((r.dateFin IS NULL AND r.dateDebut >= :dateActuelle) OR " +
                     "(r.dateFin IS NOT NULL AND r.dateFin >= :dateActuelle)) " +
                     "ORDER BY r.dateDebut ASC, r.heureDebut ASC")
       Page<Reservation> findFutureReservationsByUtilisateur(
                     @Param("utilisateurId") Long utilisateurId,
                     @Param("dateActuelle") LocalDate dateActuelle,
                     Pageable pageable);

       /**
        * Trouve les réservations passées d'un utilisateur avec pagination
        */
       @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :utilisateurId " +
                     "AND ((r.dateFin IS NULL AND r.dateDebut < :dateActuelle) OR " +
                     "(r.dateFin IS NOT NULL AND r.dateFin < :dateActuelle)) " +
                     "ORDER BY r.dateDebut DESC, r.heureDebut DESC")
       Page<Reservation> findPastReservationsByUtilisateur(
                     @Param("utilisateurId") Long utilisateurId,
                     @Param("dateActuelle") LocalDate dateActuelle,
                     Pageable pageable);

       /**
        * Trouve toutes les réservations entre deux dates
        */
       @Query("SELECT r FROM Reservation r WHERE " +
                     "((r.dateFin IS NULL AND r.dateDebut BETWEEN :dateDebut AND :dateFin) OR " +
                     "(r.dateFin IS NOT NULL AND r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut)) " +
                     "ORDER BY r.dateDebut ASC, r.heureDebut ASC")
       List<Reservation> findReservationsBetweenDates(
                     @Param("dateDebut") LocalDate dateDebut,
                     @Param("dateFin") LocalDate dateFin);

       /**
        * Compte les réservations d'un utilisateur
        */
       long countByUtilisateurId(Long utilisateurId);

       /**
        * Compte les réservations d'une salle
        */
       long countBySalleId(Long salleId);
}
