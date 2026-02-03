package com.ifri.bookmyhall.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ifri.bookmyhall.dto.SalleDTO;
import com.ifri.bookmyhall.exceptions.ResourceNotFoundException;
import com.ifri.bookmyhall.models.Salle;
import com.ifri.bookmyhall.repositories.SalleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SalleService {

    private final SalleRepository salleRepository;

    /**
     * Crée une nouvelle salle
     */
    public SalleDTO createSalle(SalleDTO salleDTO) {
        log.info("Création d'une nouvelle salle: {}", salleDTO.getNom());

        if (salleRepository.existsByNom(salleDTO.getNom())) {
            throw new IllegalArgumentException("Une salle avec ce nom existe déjà");
        }

        Salle salle = convertToEntity(salleDTO);
        
        if (salle.getDisponible() == null) {
            salle.setDisponible(true);
        }

        Salle saved = salleRepository.save(salle);
        log.info("Salle créée avec succès: ID {}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * Récupère une salle par son ID
     */
    @Transactional(readOnly = true)
    public SalleDTO getSalleById(Long id) {
        log.debug("Récupération de la salle ID: {}", id);
        
        Salle salle = salleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID: " + id));
        
        return convertToDTO(salle);
    }

    /**
     * Récupère toutes les salles
     */
    @Transactional(readOnly = true)
    public List<SalleDTO> getAllSalles() {
        log.debug("Récupération de toutes les salles");
        
        return salleRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Récupère les salles disponibles
     */
    @Transactional(readOnly = true)
    public List<SalleDTO> getSallesDisponibles() {
        log.debug("Récupération des salles disponibles");
        
        return salleRepository.findByDisponible(true).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Recherche de salles par critères multiples
     */
    @Transactional(readOnly = true)
    public List<SalleDTO> searchSalles(String localisation, Integer capaciteMin, BigDecimal prixMax) {
        log.debug("Recherche de salles - Localisation: {}, CapacitéMin: {}, PrixMax: {}", 
                  localisation, capaciteMin, prixMax);
        
        return salleRepository.searchSalles(localisation, capaciteMin, prixMax, true).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Met à jour une salle
     */
    public SalleDTO updateSalle(Long id, SalleDTO salleDTO) {
        log.info("Mise à jour de la salle ID: {}", id);
        
        Salle salle = salleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID: " + id));

        if (!salle.getNom().equals(salleDTO.getNom()) &&
            salleRepository.existsByNom(salleDTO.getNom())) {
            throw new IllegalArgumentException("Une salle avec ce nom existe déjà");
        }

        salle.setNom(salleDTO.getNom());
        salle.setCapacite(salleDTO.getCapacite());
        salle.setLocalisation(salleDTO.getLocalisation());
        salle.setDescription(salleDTO.getDescription());
        salle.setPrixParJour(salleDTO.getPrixParJour());
        salle.setImageUrl(salleDTO.getImageUrl());
        salle.setEquipements(salleDTO.getEquipements());
        salle.setDisponible(salleDTO.getDisponible());

        Salle updated = salleRepository.save(salle);
        log.info("Salle mise à jour: ID {}", updated.getId());

        return convertToDTO(updated);
    }

    /**
     * Change la disponibilité d'une salle
     */
    public SalleDTO toggleDisponibilite(Long id, Boolean disponible) {
        log.info("Modification de la disponibilité de la salle ID {}: {}", id, disponible);
        
        Salle salle = salleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID: " + id));

        salle.setDisponible(disponible);
        Salle updated = salleRepository.save(salle);

        return convertToDTO(updated);
    }

    /**
     * Supprime une salle
     */
    public void deleteSalle(Long id) {
        log.info("Suppression de la salle ID: {}", id);
        
        Salle salle = salleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID: " + id));

        if (!salle.getReservations().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer une salle avec des réservations existantes");
        }

        salleRepository.deleteById(id);
        log.info("Salle supprimée: ID {}", id);
    }

    /**
     * Compte les salles disponibles
     */
    @Transactional(readOnly = true)
    public long countSallesDisponibles() {
        return salleRepository.countSallesDisponibles();
    }

    /**
     * Convertit une entité Salle en DTO
     */
    private SalleDTO convertToDTO(Salle salle) {
        return SalleDTO.builder()
            .id(salle.getId())
            .nom(salle.getNom())
            .capacite(salle.getCapacite())
            .localisation(salle.getLocalisation())
            .description(salle.getDescription())
            .prixParJour(salle.getPrixParJour())
            .imageUrl(salle.getImageUrl())
            .equipements(salle.getEquipements())
            .disponible(salle.getDisponible())
            .nombreReservations((long) salle.getReservations().size())
            .build();
    }

    /**
     * Convertit un DTO en entité Salle
     */
    private Salle convertToEntity(SalleDTO dto) {
        return Salle.builder()
            .nom(dto.getNom())
            .capacite(dto.getCapacite())
            .localisation(dto.getLocalisation())
            .description(dto.getDescription())
            .prixParJour(dto.getPrixParJour())
            .imageUrl(dto.getImageUrl())
            .equipements(dto.getEquipements())
            .disponible(dto.getDisponible())
            .build();
    }
}
