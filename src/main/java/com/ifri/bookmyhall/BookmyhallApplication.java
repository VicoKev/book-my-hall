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

		// Message de démarrage
        log.info("==============================================");
        log.info("BookMyHall Application démarrée avec succès!");
        log.info("Accédez à l'application: http://localhost:8080");
        log.info("==============================================");
	}

	/**
     * CommandLineRunner pour initialiser les données de test
     * 
     * S'exécute automatiquement après le démarrage de l'application
     * Crée un admin par défaut et quelques salles de test
     * 
     * @param utilisateurRepository repository des utilisateurs
     * @param salleRepository repository des salles
     * @param passwordEncoder encodeur de mots de passe
     * @return CommandLineRunner qui initialise les données
     */
    @Bean
    CommandLineRunner initData(UtilisateurRepository utilisateurRepository,
                               SalleRepository salleRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            log.info("========================================");
            log.info("Initialisation des données de test...");
            log.info("========================================");

            // ========== CRÉATION DES UTILISATEURS ==========
            
            // Vérifier si des utilisateurs existent déjà
            if (utilisateurRepository.count() == 0) {
                log.info("Aucun utilisateur trouvé. Création des utilisateurs de test...");
                
                // 1. Créer l'administrateur par défaut
                Utilisateur admin = Utilisateur.builder()
                    .nom("Admin")
                    .prenom("System")
                    .email("admin@bookmyhall.com")
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .telephone("+2290123456789")
                    .role(Role.ADMIN)
                    .actif(true)
                    .build();
                utilisateurRepository.save(admin);
                log.info("✅ Admin créé - Username: admin, Password: admin123");

                // 2. Créer un utilisateur de test
                Utilisateur user = Utilisateur.builder()
                    .nom("Dupont")
                    .prenom("Jean")
                    .email("jean.dupont@example.com")
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .telephone("+2290987654321")
                    .role(Role.USER)
                    .actif(true)
                    .build();
                utilisateurRepository.save(user);
                log.info("✅ User créé - Username: user, Password: user123");
                
            } else {
                log.info("ℹ️  Utilisateurs déjà existants. Aucune création.");
            }

            // ========== CRÉATION DES SALLES ==========
            
            // Vérifier si des salles existent déjà
            if (salleRepository.count() == 0) {
                log.info("Aucune salle trouvée. Création des salles de test...");
                
                // Salle 1 : Grande salle pour événements importants
                Salle salle1 = Salle.builder()
                    .nom("Grande Salle des Fêtes")
                    .capacite(200)
                    .localisation("Centre-ville, Porto-Novo")
                    .description("Magnifique salle spacieuse idéale pour mariages et grandes cérémonies. " +
                                "Décoration élégante et équipements modernes.")
                    .prixParJour(new BigDecimal("150000"))
					.imageFileName("party-room.jpg")
                    .equipements("Climatisation, Sonorisation professionnelle, Éclairage LED, " +
                                "Tables et chaises pour 200 personnes, Scène")
                    .disponible(true)
                    .build();
                salleRepository.save(salle1);
                log.info("✅ Salle créée: {}", salle1.getNom());

                // Salle 2 : Salle VIP haut de gamme
                Salle salle2 = Salle.builder()
                    .nom("Salle VIP Premium")
                    .capacite(100)
                    .localisation("Cotonou, Akpakpa")
                    .description("Salle élégante et moderne avec équipements haut de gamme. " +
                                "Parfaite pour événements d'entreprise et réceptions privées.")
                    .prixParJour(new BigDecimal("100000"))
                    .imageFileName("vip-room.jpg")
                    .equipements("Climatisation, WiFi haut débit, Projecteur 4K, " +
                                "Système audio Bose, Mobilier design")
                    .disponible(true)
                    .build();
                salleRepository.save(salle2);
                log.info("✅ Salle créée: {}", salle2.getNom());

                // Salle 3 : Salle familiale conviviale
                Salle salle3 = Salle.builder()
                    .nom("Salle Familiale")
                    .capacite(50)
                    .localisation("Parakou, Centre")
                    .description("Salle conviviale parfaite pour événements familiaux et anniversaires. " +
                                "Ambiance chaleureuse et accueillante.")
                    .prixParJour(new BigDecimal("50000"))
					.imageFileName("party-hall.jpg")
                    .equipements("Climatisation, Cuisine équipée, Espace enfants, " +
                                "Décoration personnalisable")
                    .disponible(true)
                    .build();
                salleRepository.save(salle3);
                log.info("✅ Salle créée: {}", salle3.getNom());

                // Salle 4 : Espace pour événements professionnels
                Salle salle4 = Salle.builder()
                    .nom("Espace Conférence")
                    .capacite(150)
                    .localisation("Cotonou, Haie Vive")
                    .description("Salle professionnelle pour séminaires, formations et conférences. " +
                                "Équipements audiovisuels de pointe.")
                    .prixParJour(new BigDecimal("120000"))
					.imageFileName("large-party-room.jpg")
                    .equipements("Vidéo-projecteur HD, Écran géant, WiFi fibre optique, " +
                                "Système de visioconférence, Paperboard")
                    .disponible(true)
                    .build();
                salleRepository.save(salle4);
                log.info("✅ Salle créée: {}", salle4.getNom());

                // Salle 5 : Salle prestige pour grands événements
                Salle salle5 = Salle.builder()
                    .nom("Salle de Réception Royale")
                    .capacite(300)
                    .localisation("Abomey-Calavi")
                    .description("La plus grande salle, parfaite pour les grands événements et mariages fastueux. " +
                                "Décoration luxueuse et services haut de gamme.")
                    .prixParJour(new BigDecimal("200000"))
					.imageFileName("event-room.jpg")
                    .equipements("Scène professionnelle, Sonorisation concert, Éclairage LED RGB, " +
                                "Parking VIP 50 places, Service traiteur disponible")
                    .disponible(true)
                    .build();
                salleRepository.save(salle5);
                log.info("✅ Salle créée: {}", salle5.getNom());

                log.info("✅ {} salles de test créées avec succès", salleRepository.count());
                
            } else {
                log.info("ℹ️  Salles déjà existantes. Aucune création.");
            }

            // ========== RÉCAPITULATIF ==========
            
            log.info("========================================");
            log.info("📊 Récapitulatif des données:");
            log.info("   - Utilisateurs: {}", utilisateurRepository.count());
            log.info("   - Salles: {}", salleRepository.count());
            log.info("========================================");
            log.info("🎯 Comptes de test disponibles:");
            log.info("   👤 Admin:");
            log.info("      Username: admin");
            log.info("      Password: admin123");
            log.info("   👤 User:");
            log.info("      Username: user");
            log.info("      Password: user123");
            log.info("========================================");
            log.info("✅ Initialisation terminée avec succès!");
            log.info("========================================");
        };
    }

}
