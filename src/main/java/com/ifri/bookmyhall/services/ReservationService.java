package com.ifri.bookmyhall.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ifri.bookmyhall.dto.ReservationDTO;
import com.ifri.bookmyhall.exceptions.ResourceNotFoundException;
import com.ifri.bookmyhall.models.Reservation;
import com.ifri.bookmyhall.models.Reservation.StatutReservation;
import com.ifri.bookmyhall.models.Salle;
import com.ifri.bookmyhall.models.Utilisateur;
import com.ifri.bookmyhall.repositories.ReservationRepository;
import com.ifri.bookmyhall.repositories.SalleRepository;
import com.ifri.bookmyhall.repositories.UtilisateurRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final SalleRepository salleRepository;

    /**
     * Crée une nouvelle réservation
     */
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        log.info("Création d'une nouvelle réservation pour la salle ID: {}", reservationDTO.getSalleId());

        Utilisateur utilisateur = utilisateurRepository.findById(reservationDTO.getUtilisateurId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Salle salle = salleRepository.findById(reservationDTO.getSalleId())
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée"));

        validateReservation(reservationDTO, salle, null);

        Reservation reservation = convertToEntity(reservationDTO, utilisateur, salle);

        // Calcul du montant total basé sur le nombre de jours
        long nombreDeJours = reservationDTO.getNombreDeJours();
        reservation.setMontantTotal(salle.getPrixParJour().multiply(java.math.BigDecimal.valueOf(nombreDeJours)));

        reservation.setStatut(StatutReservation.PENDING);

        Reservation saved = reservationRepository.save(reservation);
        log.info("Réservation créée avec succès: ID {}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * Récupère une réservation par son ID
     */
    @Transactional(readOnly = true)
    public ReservationDTO getReservationById(Long id) {
        log.debug("Récupération de la réservation ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));

        return convertToDTO(reservation);
    }

    /**
     * Récupère toutes les réservations avec pagination
     */
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getAllReservations(Pageable pageable) {
        log.debug("Récupération de toutes les réservations (page: {})", pageable.getPageNumber());

        return reservationRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Récupère les réservations par statut avec pagination
     */
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getReservationsByStatut(String statut, Pageable pageable) {
        log.debug("Récupération des réservations avec statut: {} (page: {})", statut, pageable.getPageNumber());

        try {
            StatutReservation statutEnum = StatutReservation.valueOf(statut);
            return reservationRepository.findByStatut(statutEnum, pageable)
                    .map(this::convertToDTO);
        } catch (IllegalArgumentException e) {
            log.warn("Statut invalide: {}", statut);
            return getAllReservations(pageable);
        }
    }

    /**
     * Récupère les réservations d'un utilisateur avec pagination et optionnellement
     * par statut
     */
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getReservationsByUtilisateur(Long utilisateurId, String statut, Pageable pageable) {
        log.debug("Récupération des réservations de l'utilisateur ID: {} (statut: {}, page: {})",
                utilisateurId, statut, pageable.getPageNumber());

        if (statut != null && !statut.isEmpty() && !statut.equalsIgnoreCase("all")) {
            try {
                StatutReservation statutEnum = StatutReservation.valueOf(statut);
                return reservationRepository.findByUtilisateurIdAndStatut(utilisateurId, statutEnum, pageable)
                        .map(this::convertToDTO);
            } catch (IllegalArgumentException e) {
                log.warn("Statut invalide pour l'utilisateur {}: {}", utilisateurId, statut);
            }
        }

        return reservationRepository.findByUtilisateurIdOrderByDateDesc(utilisateurId, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Récupère les réservations d'un utilisateur avec pagination (Surcharge pour
     * compatibilité)
     */
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getReservationsByUtilisateur(Long utilisateurId, Pageable pageable) {
        return getReservationsByUtilisateur(utilisateurId, null, pageable);
    }

    /**
     * Récupère les réservations futures d'un utilisateur avec pagination
     */
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getFutureReservationsByUtilisateur(Long utilisateurId, Pageable pageable) {
        log.debug("Récupération des réservations futures de l'utilisateur ID: {} (page: {})", utilisateurId,
                pageable.getPageNumber());

        LocalDate today = LocalDate.now();
        return reservationRepository.findFutureReservationsByUtilisateur(utilisateurId, today, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Récupère les réservations d'une salle
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsBySalle(Long salleId) {
        log.debug("Récupération des réservations de la salle ID: {}", salleId);

        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée"));

        return reservationRepository.findBySalle(salle).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les réservations d'une salle pour une date donnée
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsBySalleAndDate(Long salleId, LocalDate date) {
        log.debug("Récupération des réservations de la salle ID {} pour le {}", salleId, date);

        return reservationRepository.findBySalleIdAndDate(salleId, date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour une réservation
     */
    public ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO) {
        log.info("Mise à jour de la réservation ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));

        Salle salle = salleRepository.findById(reservationDTO.getSalleId())
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée"));

        validateReservation(reservationDTO, salle, id);

        reservation.setDateDebut(reservationDTO.getDateDebut());
        reservation.setDateFin(reservationDTO.getDateFin());
        reservation.setHeureDebut(reservationDTO.getHeureDebut());
        reservation.setHeureFin(reservationDTO.getHeureFin());
        reservation.setTypeEvenement(reservationDTO.getTypeEvenement());
        reservation.setDescription(reservationDTO.getDescription());
        reservation.setNombrePersonnes(reservationDTO.getNombrePersonnes());
        reservation.setSalle(salle);

        // Recalculer le montant total basé sur le nombre de jours
        long nombreDeJours = reservationDTO.getNombreDeJours();
        reservation.setMontantTotal(salle.getPrixParJour().multiply(java.math.BigDecimal.valueOf(nombreDeJours)));

        Reservation updated = reservationRepository.save(reservation);
        log.info("Réservation mise à jour: ID {}", updated.getId());

        return convertToDTO(updated);
    }

    /**
     * Confirme une réservation
     */
    public ReservationDTO confirmerReservation(Long id) {
        log.info("Confirmation de la réservation ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));

        reservation.confirmerR();
        Reservation updated = reservationRepository.save(reservation);

        return convertToDTO(updated);
    }

    /**
     * Annule une réservation
     */
    public ReservationDTO annulerReservation(Long id) {
        log.info("Annulation de la réservation ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id));

        reservation.annulerR();
        Reservation updated = reservationRepository.save(reservation);

        return convertToDTO(updated);
    }

    /**
     * Supprime une réservation
     */
    public void deleteReservation(Long id) {
        log.info("Suppression de la réservation ID: {}", id);

        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Réservation non trouvée avec l'ID: " + id);
        }

        reservationRepository.deleteById(id);
        log.info("Réservation supprimée: ID {}", id);
    }

    /**
     * Valide une réservation
     */
    private void validateReservation(ReservationDTO dto, Salle salle, Long excludeId) {

        if (!salle.isReservable()) {
            throw new IllegalStateException("Cette salle n'est pas disponible pour réservation");
        }

        if (!dto.hasValidTimeRange()) {
            throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
        }

        if (!dto.hasValidDateRange()) {
            throw new IllegalArgumentException("La date de fin doit être après ou égale à la date de début");
        }

        if (dto.getNombrePersonnes() > salle.getCapacite()) {
            throw new IllegalArgumentException(
                    String.format("Le nombre de personnes (%d) dépasse la capacité de la salle (%d)",
                            dto.getNombrePersonnes(), salle.getCapacite()));
        }

        // Déterminer la plage de dates à vérifier
        LocalDate dateDebut = dto.getDateDebut();
        LocalDate dateFin = dto.getDateFin() != null ? dto.getDateFin() : dto.getDateDebut();

        boolean hasConflict;
        if (excludeId != null) {
            hasConflict = reservationRepository.existsConflictingReservationExcludingId(
                    excludeId,
                    salle.getId(),
                    dateDebut,
                    dateFin,
                    dto.getHeureDebut(),
                    dto.getHeureFin());
        } else {
            hasConflict = reservationRepository.existsConflictingReservation(
                    salle.getId(),
                    dateDebut,
                    dateFin,
                    dto.getHeureDebut(),
                    dto.getHeureFin());
        }

        if (hasConflict) {
            throw new IllegalStateException(
                    "Cette salle est déjà réservée sur ce créneau horaire");
        }
    }

    /**
     * Convertit une entité Reservation en DTO
     */
    private ReservationDTO convertToDTO(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .dateDebut(reservation.getDateDebut())
                .dateFin(reservation.getDateFin())
                .heureDebut(reservation.getHeureDebut())
                .heureFin(reservation.getHeureFin())
                .typeEvenement(reservation.getTypeEvenement())
                .description(reservation.getDescription())
                .nombrePersonnes(reservation.getNombrePersonnes())
                .montantTotal(reservation.getMontantTotal())
                .statut(reservation.getStatut())
                .utilisateurId(reservation.getUtilisateur().getId())
                .utilisateurNom(reservation.getUtilisateur().getNomComplet())
                .salleId(reservation.getSalle().getId())
                .salleNom(reservation.getSalle().getNom())
                .salleCapacite(reservation.getSalle().getCapacite())
                .build();
    }

    /**
     * Convertit un DTO en entité Reservation
     */
    private Reservation convertToEntity(ReservationDTO dto, Utilisateur utilisateur, Salle salle) {
        return Reservation.builder()
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .heureDebut(dto.getHeureDebut())
                .heureFin(dto.getHeureFin())
                .typeEvenement(dto.getTypeEvenement())
                .description(dto.getDescription())
                .nombrePersonnes(dto.getNombrePersonnes())
                .utilisateur(utilisateur)
                .salle(salle)
                .build();
    }
}
