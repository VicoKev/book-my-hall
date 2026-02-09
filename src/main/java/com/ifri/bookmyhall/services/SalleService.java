package com.ifri.bookmyhall.services;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${app.upload-dir}")
    private String uploadDir;

    /**
     * Crée une nouvelle salle
     */
    public SalleDTO createSalle(SalleDTO salleDTO, MultipartFile imageFile) {
        log.info("Création d'une nouvelle salle: {}", salleDTO.getNom());

        if (salleRepository.existsByNom(salleDTO.getNom())) {
            throw new IllegalArgumentException("Une salle avec ce nom existe déjà");
        }

        // Handle file upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveImageFile(imageFile);
            salleDTO.setImageFileName(fileName);
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
     * Saves an image file to the upload directory and returns the filename
     */
    private String saveImageFile(MultipartFile imageFile) {
        try {
            // Validate file type
            String contentType = imageFile.getContentType();
            if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType) && !"image/gif".equals(contentType)) {
                throw new IllegalArgumentException("Seuls les fichiers JPEG, PNG et GIF sont autorisés");
            }

            // Validate file size (10MB max)
            if (imageFile.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("La taille maximale du fichier est de 10MB");
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = imageFile.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            log.error("Erreur lors de l'enregistrement du fichier image", e);
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier image", e);
        }
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
    public SalleDTO updateSalle(Long id, SalleDTO salleDTO, MultipartFile imageFile) {
        log.info("Mise à jour de la salle ID: {}", id);

        Salle salle = salleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID: " + id));

        if (!salle.getNom().equals(salleDTO.getNom()) &&
            salleRepository.existsByNom(salleDTO.getNom())) {
            throw new IllegalArgumentException("Une salle avec ce nom existe déjà");
        }

        // Handle file upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            // Delete old image file if exists
            if (salle.getImageFileName() != null && !salle.getImageFileName().isEmpty()) {
                deleteImageFile(salle.getImageFileName());
            }

            // Save new image file
            String fileName = saveImageFile(imageFile);
            salleDTO.setImageFileName(fileName);
        } else {
            // Keep existing image file name
            salleDTO.setImageFileName(salle.getImageFileName());
        }

        salle.setNom(salleDTO.getNom());
        salle.setCapacite(salleDTO.getCapacite());
        salle.setLocalisation(salleDTO.getLocalisation());
        salle.setDescription(salleDTO.getDescription());
        salle.setPrixParJour(salleDTO.getPrixParJour());
        salle.setImageFileName(salleDTO.getImageFileName());
        salle.setEquipements(salleDTO.getEquipements());
        salle.setDisponible(salleDTO.getDisponible());

        Salle updated = salleRepository.save(salle);
        log.info("Salle mise à jour: ID {}", updated.getId());

        return convertToDTO(updated);
    }

    /**
     * Deletes an image file from the upload directory
     */
    private void deleteImageFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Erreur lors de la suppression du fichier image: {}", fileName, e);
        }
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
            .imageFileName(salle.getImageFileName())
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
            .imageFileName(dto.getImageFileName())
            .equipements(dto.getEquipements())
            .disponible(dto.getDisponible())
            .build();
    }
}
