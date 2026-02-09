package com.ifri.bookmyhall;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ifri.bookmyhall.models.Role;
import com.ifri.bookmyhall.models.Salle;
import com.ifri.bookmyhall.models.Utilisateur;
import com.ifri.bookmyhall.repositories.SalleRepository;
import com.ifri.bookmyhall.repositories.UtilisateurRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class BookmyhallApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookmyhallApplication.class, args);
        log.info("Bienvenue sur l'application BookMyHall !");
    }

    /**
     * Initialisation des données de test au démarrage
     */
    @Bean
    CommandLineRunner initData(UtilisateurRepository utilisateurRepository,
            SalleRepository salleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (utilisateurRepository.count() == 0) {
                log.info("Initialisation des utilisateurs de test...");

                utilisateurRepository.save(Utilisateur.builder()
                        .nom("Admin").prenom("BookMyHall").email("admin@bookmyhall.com")
                        .username("admin").password(passwordEncoder.encode("admin123"))
                        .telephone("+2290123456789").role(Role.ADMIN).actif(true).build());

                utilisateurRepository.save(Utilisateur.builder()
                        .nom("Doe").prenom("John").email("john.doe@example.com")
                        .username("john").password(passwordEncoder.encode("john123"))
                        .telephone("+2290987654321").role(Role.USER).actif(true).build());

                log.info("Utilisateurs créés (admin/admin123, john/john123)");
            }

            if (salleRepository.count() == 0) {
                log.info("Initialisation des salles de test...");

                salleRepository.save(Salle.builder()
                        .nom("Grande Salle des Fêtes").capacite(200)
                        .localisation("Centre-ville, Porto-Novo")
                        .description("Idéale pour mariages et cérémonies.")
                        .prixParJour(new BigDecimal("150000")).imageFileName("party-room.jpg")
                        .equipements("Climatisation, Sonorisation, Éclairage LED").disponible(true).build());

                salleRepository.save(Salle.builder()
                        .nom("Salle VIP Premium").capacite(100)
                        .localisation("Cotonou, Akpakpa")
                        .description("Élégante et moderne avec équipements haut de gamme.")
                        .prixParJour(new BigDecimal("100000")).imageFileName("vip-room.jpg")
                        .equipements("WiFi fibre, Projecteur 4K, Audio Bose").disponible(true).build());

                salleRepository.save(Salle.builder()
                        .nom("Salle Familiale").capacite(50)
                        .localisation("Parakou, Centre")
                        .description("Conviviale parfaite pour événements familiaux.")
                        .prixParJour(new BigDecimal("50000")).imageFileName("party-hall.jpg")
                        .equipements("Cuisine équipée, Espace enfants").disponible(true).build());

                salleRepository.save(Salle.builder()
                        .nom("Espace Conférence").capacite(150)
                        .localisation("Cotonou, Haie Vive")
                        .description("Salle professionnelle pour séminaires et formations.")
                        .prixParJour(new BigDecimal("120000")).imageFileName("large-party-room.jpg")
                        .equipements("Vidéo-projecteur, Écran géant, WiFi fibre").disponible(true).build());

                salleRepository.save(Salle.builder()
                        .nom("Salle de Réception Royale").capacite(300)
                        .localisation("Abomey-Calavi")
                        .description("Grande salle pour mariages fastueux.")
                        .prixParJour(new BigDecimal("200000")).imageFileName("event-room.jpg")
                        .equipements("Scène, Sonorisation concert, Parking VIP").disponible(true).build());

                log.info("Salles de test créées");
            }
        };
    }
}
