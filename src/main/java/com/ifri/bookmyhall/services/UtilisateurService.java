package com.ifri.bookmyhall.services;

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
/** Service pour la gestion des utilisateurs (inscription, CRUD, rôles). */
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    /** Crée un nouvel utilisateur avec encodage du mot de passe. */
    public UtilisateurDTO createUtilisateur(UtilisateurDTO dto) {
        if (utilisateurRepository.existsByUsername(dto.getUsername()))
            throw new IllegalArgumentException("Username existe déjà");
        if (utilisateurRepository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("Email existe déjà");

        Utilisateur user = convertToEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (user.getRole() == null)
            user.setRole(Role.USER);
        if (user.getActif() == null)
            user.setActif(true);

        Utilisateur saved = utilisateurRepository.save(user);
        log.info("Utilisateur créé : {}", saved.getUsername());
        return convertToDTO(saved);
    }

    /** Inscrit un nouvel utilisateur avec le rôle USER. */
    public UtilisateurDTO registerUtilisateur(UtilisateurDTO dto) {
        dto.setRole(Role.USER);
        dto.setActif(true);
        return createUtilisateur(dto);
    }

    /** Récupère un utilisateur par son identifiant. */
    @Transactional(readOnly = true)
    public UtilisateurDTO getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé : " + id));
    }

    /** Récupère un utilisateur par son nom d'utilisateur. */
    @Transactional(readOnly = true)
    public UtilisateurDTO getUtilisateurByUsername(String username) {
        return utilisateurRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé : " + username));
    }

    /** Récupère tous les utilisateurs avec pagination. */
    @Transactional(readOnly = true)
    public Page<UtilisateurDTO> getAllUtilisateurs(Pageable pageable) {
        return utilisateurRepository.findAll(pageable).map(this::convertToDTO);
    }

    /** Récupère les utilisateurs filtrés par rôle. */
    @Transactional(readOnly = true)
    public Page<UtilisateurDTO> getUtilisateursByRole(Role role, Pageable pageable) {
        return utilisateurRepository.findByRole(role, pageable).map(this::convertToDTO);
    }

    /** Met à jour les informations d'un utilisateur existant. */
    public UtilisateurDTO updateUtilisateur(Long id, UtilisateurDTO dto) {
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé : " + id));

        if (!user.getUsername().equals(dto.getUsername()) && utilisateurRepository.existsByUsername(dto.getUsername()))
            throw new IllegalArgumentException("Username existe déjà");
        if (!user.getEmail().equals(dto.getEmail()) && utilisateurRepository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("Email existe déjà");

        user.setNom(dto.getNom());
        user.setPrenom(dto.getPrenom());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setTelephone(dto.getTelephone());
        user.setRole(dto.getRole());
        user.setActif(dto.getActif());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Utilisateur updated = utilisateurRepository.save(user);
        log.info("Utilisateur mis à jour : {}", id);
        return convertToDTO(updated);
    }

    /** Modifie le rôle d'un utilisateur. */
    public UtilisateurDTO changeRole(Long id, Role newRole) {
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé : " + id));
        user.setRole(newRole);
        return convertToDTO(utilisateurRepository.save(user));
    }

    /** Active ou désactive un compte utilisateur. */
    public UtilisateurDTO toggleActif(Long id, Boolean actif) {
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé : " + id));
        user.setActif(actif);
        return convertToDTO(utilisateurRepository.save(user));
    }

    /** Supprime un utilisateur de la base de données. */
    public void deleteUtilisateur(Long id) {
        if (!utilisateurRepository.existsById(id))
            throw new ResourceNotFoundException("Utilisateur non trouvé : " + id);
        utilisateurRepository.deleteById(id);
        log.info("Utilisateur supprimé : {}", id);
    }

    /** Compte le nombre d'utilisateurs par rôle. */
    @Transactional(readOnly = true)
    public long countByRole(Role role) {
        return utilisateurRepository.countByRole(role);
    }

    /** Convertit une entité en DTO. */
    private UtilisateurDTO convertToDTO(Utilisateur user) {
        return UtilisateurDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .username(user.getUsername())
                .telephone(user.getTelephone())
                .role(user.getRole())
                .actif(user.getActif())
                .nomComplet(user.getNomComplet())
                .build();
    }

    /** Convertit un DTO en entité. */
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
