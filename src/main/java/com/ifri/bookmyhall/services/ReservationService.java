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
/**
 * Service pour la gestion des réservations (création, validation, annulation).
 */
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final SalleRepository salleRepository;

    /** Crée une nouvelle réservation après validation. */
    public ReservationDTO createReservation(ReservationDTO dto) {
        Utilisateur user = utilisateurRepository.findById(dto.getUtilisateurId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        Salle salle = salleRepository.findById(dto.getSalleId())
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée"));

        validateReservation(dto, salle, null);

        Reservation res = convertToEntity(dto, user, salle);
        res.setMontantTotal(salle.getPrixParJour().multiply(java.math.BigDecimal.valueOf(dto.getNombreDeJours())));
        res.setStatut(StatutReservation.PENDING);

        Reservation saved = reservationRepository.save(res);
        log.info("Réservation créée : {}", saved.getId());
        return convertToDTO(saved);
    }

    /** Récupère une réservation par son identifiant. */
    @Transactional(readOnly = true)
    public ReservationDTO getReservationById(Long id) {
        return reservationRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée : " + id));
    }

    /** Récupère toutes les réservations avec pagination. */
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable).map(this::convertToDTO);
    }

    /** Récupère les réservations filtrées par statut. */
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getReservationsByStatut(String statut, Pageable pageable) {
        try {
            return reservationRepository.findByStatut(StatutReservation.valueOf(statut), pageable)
                    .map(this::convertToDTO);
        } catch (Exception e) {
            return getAllReservations(pageable);
        }
    }

    /** Récupère les réservations d'un utilisateur avec filtrage optionnel. */
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getReservationsByUtilisateur(Long userId, String statut, Pageable pageable) {
        if (statut != null && !statut.isEmpty() && !statut.equalsIgnoreCase("all")) {
            try {
                return reservationRepository
                        .findByUtilisateurIdAndStatut(userId, StatutReservation.valueOf(statut), pageable)
                        .map(this::convertToDTO);
            } catch (Exception e) {
            }
        }
        return reservationRepository.findByUtilisateurIdOrderByDateDesc(userId, pageable).map(this::convertToDTO);
    }

    /** Récupère les réservations d'un utilisateur (raccourci). */
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getReservationsByUtilisateur(Long userId, Pageable pageable) {
        return getReservationsByUtilisateur(userId, null, pageable);
    }

    /** Récupère les réservations à venir d'un utilisateur. */
    @Transactional(readOnly = true)
    public Page<ReservationDTO> getFutureReservationsByUtilisateur(Long userId, Pageable pageable) {
        return reservationRepository.findFutureReservationsByUtilisateur(userId, LocalDate.now(), pageable)
                .map(this::convertToDTO);
    }

    /** Récupère toutes les réservations pour une salle donnée. */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsBySalle(Long id) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée"));
        return reservationRepository.findBySalle(salle).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /** Récupère les réservations d'une salle pour une date précise. */
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsBySalleAndDate(Long id, LocalDate date) {
        return reservationRepository.findBySalleIdAndDate(id, date).stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /** Met à jour une réservation existante. */
    public ReservationDTO updateReservation(Long id, ReservationDTO dto) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée : " + id));
        Salle salle = salleRepository.findById(dto.getSalleId())
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée"));

        validateReservation(dto, salle, id);

        res.setDateDebut(dto.getDateDebut());
        res.setDateFin(dto.getDateFin());
        res.setHeureDebut(dto.getHeureDebut());
        res.setHeureFin(dto.getHeureFin());
        res.setTypeEvenement(dto.getTypeEvenement());
        res.setDescription(dto.getDescription());
        res.setNombrePersonnes(dto.getNombrePersonnes());
        res.setSalle(salle);
        res.setMontantTotal(salle.getPrixParJour().multiply(java.math.BigDecimal.valueOf(dto.getNombreDeJours())));

        log.info("Réservation mise à jour : {}", id);
        return convertToDTO(reservationRepository.save(res));
    }

    /** Confirme une réservation (statut CONFIRMED). */
    public ReservationDTO confirmerReservation(Long id) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée : " + id));
        res.confirmerR();
        return convertToDTO(reservationRepository.save(res));
    }

    /** Annule une réservation (statut CANCELLED). */
    public ReservationDTO annulerReservation(Long id) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée : " + id));
        res.annulerR();
        return convertToDTO(reservationRepository.save(res));
    }

    /** Supprime une réservation de la base de données. */
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id))
            throw new ResourceNotFoundException("Réservation non trouvée : " + id);
        reservationRepository.deleteById(id);
        log.info("Réservation supprimée : {}", id);
    }

    /** Vérifie la validité d'une réservation (dispo, capacité, dates). */
    private void validateReservation(ReservationDTO dto, Salle salle, Long excludeId) {
        if (!salle.isReservable())
            throw new IllegalStateException("Salle indisponible");
        if (!dto.hasValidTimeRange())
            throw new IllegalArgumentException("Heure de fin invalide");
        if (!dto.hasValidDateRange())
            throw new IllegalArgumentException("Date de fin invalide");
        if (dto.getNombrePersonnes() > salle.getCapacite())
            throw new IllegalArgumentException("Dépassement capacité");

        LocalDate debut = dto.getDateDebut();
        LocalDate fin = dto.getDateFin() != null ? dto.getDateFin() : debut;

        boolean conflict = excludeId != null
                ? reservationRepository.existsConflictingReservationExcludingId(excludeId, salle.getId(), debut, fin,
                        dto.getHeureDebut(), dto.getHeureFin())
                : reservationRepository.existsConflictingReservation(salle.getId(), debut, fin, dto.getHeureDebut(),
                        dto.getHeureFin());

        if (conflict)
            throw new IllegalStateException("Conflit de créneau");
    }

    /** Convertit une entité en DTO. */
    private ReservationDTO convertToDTO(Reservation res) {
        return ReservationDTO.builder()
                .id(res.getId()).dateDebut(res.getDateDebut()).dateFin(res.getDateFin())
                .heureDebut(res.getHeureDebut()).heureFin(res.getHeureFin())
                .typeEvenement(res.getTypeEvenement()).description(res.getDescription())
                .nombrePersonnes(res.getNombrePersonnes()).montantTotal(res.getMontantTotal())
                .statut(res.getStatut()).utilisateurId(res.getUtilisateur().getId())
                .utilisateurNom(res.getUtilisateur().getNomComplet())
                .salleId(res.getSalle().getId()).salleNom(res.getSalle().getNom())
                .salleCapacite(res.getSalle().getCapacite()).build();
    }

    /** Convertit un DTO en entité. */
    private Reservation convertToEntity(ReservationDTO dto, Utilisateur user, Salle salle) {
        return Reservation.builder()
                .dateDebut(dto.getDateDebut()).dateFin(dto.getDateFin())
                .heureDebut(dto.getHeureDebut()).heureFin(dto.getHeureFin())
                .typeEvenement(dto.getTypeEvenement()).description(dto.getDescription())
                .nombrePersonnes(dto.getNombrePersonnes()).utilisateur(user).salle(salle).build();
    }
}
