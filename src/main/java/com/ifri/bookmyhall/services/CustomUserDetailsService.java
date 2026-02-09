package com.ifri.bookmyhall.services;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ifri.bookmyhall.models.Utilisateur;
import com.ifri.bookmyhall.repositories.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
/** Service pour charger les détails de l'utilisateur pour Spring Security. */
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    /** Charge un utilisateur par son nom d'utilisateur. */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));

        if (!utilisateur.getActif()) {
            throw new UsernameNotFoundException("Compte désactivé: " + username);
        }

        return new User(
                utilisateur.getUsername(),
                utilisateur.getPassword(),
                utilisateur.getActif(),
                true, true, true,
                getAuthorities(utilisateur));
    }

    /** Convertit le rôle de l'utilisateur en authority. */
    private Collection<? extends GrantedAuthority> getAuthorities(Utilisateur utilisateur) {
        return Collections.singletonList(new SimpleGrantedAuthority(utilisateur.getRole().name()));
    }
}
