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
/** Repository pour l'accès aux données des réservations. */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

       /** Récupère toutes les réservations d'un utilisateur. */
       List<Reservation> findByUtilisateur(Utilisateur utilisateur);

       /** Récupère toutes les réservations pour une salle. */
       List<Reservation> findBySalle(Salle salle);

       /** Récupère les réservations par statut avec pagination. */
       Page<Reservation> findByStatut(StatutReservation statut, Pageable pageable);

       /** Liste les réservations d'un utilisateur triées par date décroissante. */
       @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :utilisateurId " +
                     "ORDER BY r.dateDebut DESC, r.heureDebut DESC")
       Page<Reservation> findByUtilisateurIdOrderByDateDesc(@Param("utilisateurId") Long utilisateurId,
                     Pageable pageable);

       /** Liste les réservations d'un utilisateur filtrées par statut. */
       @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :utilisateurId AND r.statut = :statut " +
                     "ORDER BY r.dateDebut DESC, r.heureDebut DESC")
       Page<Reservation> findByUtilisateurIdAndStatut(@Param("utilisateurId") Long utilisateurId,
                     @Param("statut") StatutReservation statut,
                     Pageable pageable);

       /** Récupère les réservations d'une salle pour une date donnée. */
       @Query("SELECT r FROM Reservation r WHERE r.salle.id = :salleId " +
                     "AND ((r.dateFin IS NULL AND r.dateDebut = :date) OR " +
                     "(r.dateFin IS NOT NULL AND r.dateDebut <= :date AND r.dateFin >= :date)) " +
                     "ORDER BY r.heureDebut ASC")
       List<Reservation> findBySalleIdAndDate(@Param("salleId") Long salleId, @Param("date") LocalDate date);

       /** Vérifie s'il existe une réservation concurrente sur un créneau. */
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

       /** Vérifie les conflits de créneaux en excluant une réservation spécifique. */
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

       /** Récupère les réservations futures d'un utilisateur. */
       @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :utilisateurId " +
                     "AND ((r.dateFin IS NULL AND r.dateDebut >= :dateActuelle) OR " +
                     "(r.dateFin IS NOT NULL AND r.dateFin >= :dateActuelle)) " +
                     "ORDER BY r.dateDebut ASC, r.heureDebut ASC")
       Page<Reservation> findFutureReservationsByUtilisateur(
                     @Param("utilisateurId") Long utilisateurId,
                     @Param("dateActuelle") LocalDate dateActuelle,
                     Pageable pageable);

       /** Récupère l'historique des réservations d'un utilisateur. */
       @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :utilisateurId " +
                     "AND ((r.dateFin IS NULL AND r.dateDebut < :dateActuelle) OR " +
                     "(r.dateFin IS NOT NULL AND r.dateFin < :dateActuelle)) " +
                     "ORDER BY r.dateDebut DESC, r.heureDebut DESC")
       Page<Reservation> findPastReservationsByUtilisateur(
                     @Param("utilisateurId") Long utilisateurId,
                     @Param("dateActuelle") LocalDate dateActuelle,
                     Pageable pageable);

       /** Recherche les réservations dans une plage de dates. */
       @Query("SELECT r FROM Reservation r WHERE " +
                     "((r.dateFin IS NULL AND r.dateDebut BETWEEN :dateDebut AND :dateFin) OR " +
                     "(r.dateFin IS NOT NULL AND r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut)) " +
                     "ORDER BY r.dateDebut ASC, r.heureDebut ASC")
       List<Reservation> findReservationsBetweenDates(
                     @Param("dateDebut") LocalDate dateDebut,
                     @Param("dateFin") LocalDate dateFin);

       /** Compte le nombre total de réservations pour un utilisateur. */
       long countByUtilisateurId(Long utilisateurId);

       /** Compte le nombre total de réservations pour une salle. */
       long countBySalleId(Long salleId);
}
