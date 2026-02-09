package com.ifri.bookmyhall.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ifri.bookmyhall.dto.UtilisateurDTO;
import com.ifri.bookmyhall.exceptions.ResourceNotFoundException;
import com.ifri.bookmyhall.models.Role;
import com.ifri.bookmyhall.models.Utilisateur;
import com.ifri.bookmyhall.repositories.UtilisateurRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crée un nouvel utilisateur
     */
    public UtilisateurDTO createUtilisateur(UtilisateurDTO UtilisateurDTO) {
        log.info("Création d'un nouvel utilisateur: {}", UtilisateurDTO.getUsername());

        if (utilisateurRepository.existsByUsername(UtilisateurDTO.getUsername())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà");
        }

        if (utilisateurRepository.existsByEmail(UtilisateurDTO.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        Utilisateur utilisateur = convertToEntity(UtilisateurDTO);

        utilisateur.setPassword(passwordEncoder.encode(UtilisateurDTO.getPassword()));

        if (utilisateur.getRole() == null) {
            utilisateur.setRole(Role.USER);
        }
        if (utilisateur.getActif() == null) {
            utilisateur.setActif(true);
        }

        Utilisateur saved = utilisateurRepository.save(utilisateur);
        log.info("Utilisateur créé avec succès: ID {}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * Enregistre un nouvel utilisateur
     */
    public UtilisateurDTO registerUtilisateur(UtilisateurDTO UtilisateurDTO) {
        log.info("Inscription d'un nouvel utilisateur: {}", UtilisateurDTO.getUsername());

        UtilisateurDTO.setRole(Role.USER);
        UtilisateurDTO.setActif(true);

        return createUtilisateur(UtilisateurDTO);
    }

    /**
     * Récupère un utilisateur par son ID
     */
    @Transactional(readOnly = true)
    public UtilisateurDTO getUtilisateurById(Long id) {
        log.debug("Récupération de l'utilisateur ID: {}", id);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        return convertToDTO(utilisateur);
    }

    /**
     * Récupère un utilisateur par son username
     */
    @Transactional(readOnly = true)
    public UtilisateurDTO getUtilisateurByUsername(String username) {
        log.debug("Récupération de l'utilisateur: {}", username);

        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + username));

        return convertToDTO(utilisateur);
    }

    /**
     * Récupère tous les utilisateurs avec pagination
     */
    @Transactional(readOnly = true)
    public Page<UtilisateurDTO> getAllUtilisateurs(Pageable pageable) {
        log.debug("Récupération de tous les utilisateurs (page: {})", pageable.getPageNumber());

        return utilisateurRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Récupère les utilisateurs par rôle avec pagination
     */
    @Transactional(readOnly = true)
    public Page<UtilisateurDTO> getUtilisateursByRole(Role role, Pageable pageable) {
        log.debug("Récupération des utilisateurs avec le rôle: {} (page: {})", role, pageable.getPageNumber());

        return utilisateurRepository.findByRole(role, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Met à jour un utilisateur
     */
    public UtilisateurDTO updateUtilisateur(Long id, UtilisateurDTO UtilisateurDTO) {
        log.info("Mise à jour de l'utilisateur ID: {}", id);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        if (!utilisateur.getUsername().equals(UtilisateurDTO.getUsername()) &&
                utilisateurRepository.existsByUsername(UtilisateurDTO.getUsername())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà");
        }

        if (!utilisateur.getEmail().equals(UtilisateurDTO.getEmail()) &&
                utilisateurRepository.existsByEmail(UtilisateurDTO.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        utilisateur.setNom(UtilisateurDTO.getNom());
        utilisateur.setPrenom(UtilisateurDTO.getPrenom());
        utilisateur.setEmail(UtilisateurDTO.getEmail());
        utilisateur.setUsername(UtilisateurDTO.getUsername());
        utilisateur.setTelephone(UtilisateurDTO.getTelephone());
        utilisateur.setRole(UtilisateurDTO.getRole());
        utilisateur.setActif(UtilisateurDTO.getActif());

        if (UtilisateurDTO.getPassword() != null && !UtilisateurDTO.getPassword().isEmpty()) {
            utilisateur.setPassword(passwordEncoder.encode(UtilisateurDTO.getPassword()));
        }

        Utilisateur updated = utilisateurRepository.save(utilisateur);
        log.info("Utilisateur mis à jour: ID {}", updated.getId());

        return convertToDTO(updated);
    }

    /**
     * Change le rôle d'un utilisateur
     */
    public UtilisateurDTO changeRole(Long id, Role newRole) {
        log.info("Changement de rôle pour l'utilisateur ID {}: {}", id, newRole);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        utilisateur.setRole(newRole);
        Utilisateur updated = utilisateurRepository.save(utilisateur);

        return convertToDTO(updated);
    }

    /**
     * Active ou désactive un utilisateur
     */
    public UtilisateurDTO toggleActif(Long id, Boolean actif) {
        log.info("Modification du statut actif pour l'utilisateur ID {}: {}", id, actif);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        utilisateur.setActif(actif);
        Utilisateur updated = utilisateurRepository.save(utilisateur);

        return convertToDTO(updated);
    }

    /**
     * Supprime un utilisateur
     */
    public void deleteUtilisateur(Long id) {
        log.info("Suppression de l'utilisateur ID: {}", id);

        if (!utilisateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        }

        utilisateurRepository.deleteById(id);
        log.info("Utilisateur supprimé: ID {}", id);
    }

    /**
     * Compte les utilisateurs par rôle
     */
    @Transactional(readOnly = true)
    public long countByRole(Role role) {
        return utilisateurRepository.countByRole(role);
    }

    /**
     * Convertit une entité Utilisateur en DTO
     */
    private UtilisateurDTO convertToDTO(Utilisateur utilisateur) {
        return UtilisateurDTO.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .username(utilisateur.getUsername())
                .telephone(utilisateur.getTelephone())
                .role(utilisateur.getRole())
                .actif(utilisateur.getActif())
                .nomComplet(utilisateur.getNomComplet())
                .build();
    }

    /**
     * Convertit un DTO en entité Utilisateur
     */
    private Utilisateur convertToEntity(UtilisateurDTO dto) {
        return Utilisateur.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .telephone(dto.getTelephone())
                .role(dto.getRole())
                .actif(dto.getActif())
                .build();
    }
}
