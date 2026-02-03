package com.ifri.bookmyhall.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ifri.bookmyhall.models.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

}
