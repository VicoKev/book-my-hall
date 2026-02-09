package com.ifri.bookmyhall.services;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
/** Service pour la gestion des salles (CRUD, recherche, images). */
public class SalleService {

    private final SalleRepository salleRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    /** Crée une nouvelle salle avec une image optionnelle. */
    public SalleDTO createSalle(SalleDTO dto, MultipartFile file) {
        if (salleRepository.existsByNom(dto.getNom()))
            throw new IllegalArgumentException("Nom déjà utilisé");

        if (file != null && !file.isEmpty())
            dto.setImageFileName(saveImageFile(file));

        Salle salle = convertToEntity(dto);
        if (salle.getDisponible() == null)
            salle.setDisponible(true);

        Salle saved = salleRepository.save(salle);
        log.info("Salle créée : {}", saved.getNom());
        return convertToDTO(saved);
    }

    /** Enregistre un fichier image sur le disque. */
    private String saveImageFile(MultipartFile file) {
        try {
            String ct = file.getContentType();
            if (ct == null || (!ct.contains("image/jpeg") && !ct.contains("image/png") && !ct.contains("image/gif")))
                throw new IllegalArgumentException("Format invalide");

            if (file.getSize() > 10 * 1024 * 1024)
                throw new IllegalArgumentException("Fichier trop lourd");

            Path path = Paths.get(uploadDir);
            if (!Files.exists(path))
                Files.createDirectories(path);

            String name = UUID.randomUUID().toString() + getExtension(file.getOriginalFilename());
            Files.copy(file.getInputStream(), path.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            return name;
        } catch (IOException e) {
            log.error("Erreur sauvegarde image", e);
            throw new RuntimeException("Erreur sauvegarde image", e);
        }
    }

    /** Extrait l'extension d'un nom de fichier. */
    private String getExtension(String filename) {
        return (filename != null && filename.contains(".")) ? filename.substring(filename.lastIndexOf(".")) : "";
    }

    /** Récupère une salle par son identifiant. */
    @Transactional(readOnly = true)
    public SalleDTO getSalleById(Long id) {
        return salleRepository.findById(id).map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée : " + id));
    }

    /** Récupère toutes les salles avec pagination. */
    @Transactional(readOnly = true)
    public Page<SalleDTO> getAllSalles(Pageable p) {
        return salleRepository.findAll(p).map(this::convertToDTO);
    }

    /** Récupère les salles disponibles avec pagination. */
    @Transactional(readOnly = true)
    public Page<SalleDTO> getSallesDisponibles(Pageable p) {
        return salleRepository.findByDisponible(true, p).map(this::convertToDTO);
    }

    /** Recherche des salles selon plusieurs critères. */
    @Transactional(readOnly = true)
    public Page<SalleDTO> searchSalles(String loc, Integer cap, BigDecimal prix, Pageable p) {
        return salleRepository.searchSalles(loc, cap, prix, true, p).map(this::convertToDTO);
    }

    /** Met à jour les informations d'une salle existante. */
    public SalleDTO updateSalle(Long id, SalleDTO dto, MultipartFile file) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée : " + id));

        if (!salle.getNom().equals(dto.getNom()) && salleRepository.existsByNom(dto.getNom()))
            throw new IllegalArgumentException("Nom déjà utilisé");

        if (file != null && !file.isEmpty()) {
            if (salle.getImageFileName() != null)
                deleteImageFile(salle.getImageFileName());
            dto.setImageFileName(saveImageFile(file));
        } else {
            dto.setImageFileName(salle.getImageFileName());
        }

        salle.setNom(dto.getNom());
        salle.setCapacite(dto.getCapacite());
        salle.setLocalisation(dto.getLocalisation());
        salle.setDescription(dto.getDescription());
        salle.setPrixParJour(dto.getPrixParJour());
        salle.setImageFileName(dto.getImageFileName());
        salle.setEquipements(dto.getEquipements());
        salle.setDisponible(dto.getDisponible());

        log.info("Salle mise à jour : {}", id);
        return convertToDTO(salleRepository.save(salle));
    }

    /** Supprime un fichier image du disque. */
    private void deleteImageFile(String name) {
        try {
            Files.deleteIfExists(Paths.get(uploadDir).resolve(name));
        } catch (IOException e) {
            log.error("Erreur suppression image {}", name, e);
        }
    }

    /** Modifie le statut de disponibilité d'une salle. */
    public SalleDTO toggleDisponibilite(Long id, Boolean disp) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée : " + id));
        salle.setDisponible(disp);
        return convertToDTO(salleRepository.save(salle));
    }

    /** Supprime une salle si elle n'a pas de réservations. */
    public void deleteSalle(Long id) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée : " + id));
        if (!salle.getReservations().isEmpty())
            throw new IllegalStateException("Réservations en cours");
        salleRepository.deleteById(id);
        log.info("Salle supprimée : {}", id);
    }

    /** Compte le nombre total de salles disponibles. */
    @Transactional(readOnly = true)
    public long countSallesDisponibles() {
        return salleRepository.countSallesDisponibles();
    }

    /** Convertit une entité en DTO. */
    private SalleDTO convertToDTO(Salle s) {
        return SalleDTO.builder().id(s.getId()).nom(s.getNom()).capacite(s.getCapacite())
                .localisation(s.getLocalisation()).description(s.getDescription())
                .prixParJour(s.getPrixParJour()).imageFileName(s.getImageFileName())
                .equipements(s.getEquipements()).disponible(s.getDisponible())
                .nombreReservations((long) s.getReservations().size()).build();
    }

    /** Convertit un DTO en entité. */
    private Salle convertToEntity(SalleDTO dto) {
        return Salle.builder().nom(dto.getNom()).capacite(dto.getCapacite())
                .localisation(dto.getLocalisation()).description(dto.getDescription())
                .prixParJour(dto.getPrixParJour()).imageFileName(dto.getImageFileName())
                .equipements(dto.getEquipements()).disponible(dto.getDisponible()).build();
    }
}
