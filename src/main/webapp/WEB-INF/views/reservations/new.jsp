<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Nouvelle Réservation" scope="request"/>
<jsp:include page="../common/header.jsp" />

<!-- Breadcrumb -->
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/salles">Salles</a>
        </li>
        <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/salles/${salle.id}">${salle.nom}</a>
        </li>
        <li class="breadcrumb-item active">Réservation</li>
    </ol>
</nav>

<div class="row mb-4">
    <!-- Formulaire -->
    <div class="col-lg-8">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">
                    <i class="bi bi-calendar-plus"></i> Réserver : ${salle.nom}
                </h5>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/reservations/create" 
                      method="post" id="reservationForm">
                    
                    <!-- Champs cachés -->
                    <input type="hidden" name="salleId" value="${salle.id}">
                    <input type="hidden" name="utilisateurId" value="${user.id}">

                    <!-- Dates de réservation -->
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="dateDebut" class="form-label">
                                <i class="bi bi-calendar-event"></i> Date de début *
                            </label>
                            <input type="date" class="form-control" id="dateDebut" 
                                   name="dateDebut" 
                                   value="${reservationDTO.dateDebut}"
                                   min="${reservationDTO.dateDebut}"
                                   required>
                            <small class="form-text text-muted">
                                La date doit être dans le futur
                            </small>
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="dateFin" class="form-label">
                                <i class="bi bi-calendar-check"></i> Date de fin
                            </label>
                            <input type="date" class="form-control" id="dateFin" 
                                   name="dateFin" 
                                   value="${reservationDTO.dateFin}"
                                   min="${reservationDTO.dateDebut}">
                            <small class="form-text text-muted">
                                Optionnel - Laissez vide pour un seul jour
                            </small>
                        </div>
                    </div>

                    <!-- Horaires -->
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="heureDebut" class="form-label">
                                <i class="bi bi-clock"></i> Heure de début *
                            </label>
                            <input type="time" class="form-control" id="heureDebut" 
                                   name="heureDebut" required>
                            <small class="form-text text-muted">Format: HH:MM</small>
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="heureFin" class="form-label">
                                <i class="bi bi-clock-fill"></i> Heure de fin *
                            </label>
                            <input type="time" class="form-control" id="heureFin" 
                                   name="heureFin" required>
                            <small class="form-text text-muted">Doit être après l'heure de début</small>
                        </div>
                    </div>

                    <!-- Type d'événement -->
                    <div class="mb-3">
                        <label for="typeEvenement" class="form-label">
                            <i class="bi bi-tag"></i> Type d'événement *
                        </label>
                        <select class="form-select" id="typeEvenement" name="typeEvenement" required>
                            <option value="">-- Choisissez un type --</option>
                            <option value="Mariage">Mariage</option>
                            <option value="Anniversaire">Anniversaire</option>
                            <option value="Conférence">Conférence</option>
                            <option value="Séminaire">Séminaire</option>
                            <option value="Formation">Formation</option>
                            <option value="Réception">Réception</option>
                            <option value="Baptême">Baptême</option>
                            <option value="Soirée d'entreprise">Soirée d'entreprise</option>
                            <option value="Autre">Autre</option>
                        </select>
                    </div>

                    <!-- Nombre de personnes -->
                    <div class="mb-3">
                        <label for="nombrePersonnes" class="form-label">
                            <i class="bi bi-people"></i> Nombre de personnes *
                        </label>
                        <input type="number" class="form-control" id="nombrePersonnes" 
                               name="nombrePersonnes" min="1" max="${salle.capacite}" required>
                        <small class="form-text text-muted">
                            Capacité maximale: <strong>${salle.capacite}</strong> personnes
                        </small>
                    </div>

                    <!-- Description -->
                    <div class="mb-3">
                        <label for="description" class="form-label">
                            <i class="bi bi-card-text"></i> Description de l'événement
                        </label>
                        <textarea class="form-control" id="description" name="description" 
                                  rows="4" maxlength="500" 
                                  placeholder="Donnez plus de détails sur votre événement..."></textarea>
                        <small class="form-text text-muted">Maximum 500 caractères</small>
                    </div>

                    <!-- Message d'avertissement -->
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle"></i>
                        <strong>Note:</strong> Votre réservation sera en attente de confirmation 
                        par un administrateur. Vous serez notifié une fois confirmée.
                    </div>

                    <!-- Boutons -->
                    <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                        <a href="${pageContext.request.contextPath}/salles/${salle.id}" 
                           class="btn btn-outline-secondary">
                            <i class="bi bi-x-circle"></i> Annuler
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-check-circle"></i> Confirmer la réservation
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Sidebar - Récapitulatif -->
    <div class="col-lg-4">
        <!-- Informations sur la salle -->
        <div class="card mb-4">
            <div class="card-header bg-success text-white">
                <h6 class="mb-0"><i class="bi bi-building"></i> Récapitulatif</h6>
            </div>
            <div class="card-body">
                <h5 class="card-title">${salle.nom}</h5>
                
                <ul class="list-unstyled">
                    <li class="mb-2">
                        <i class="bi bi-geo-alt text-primary"></i>
                        <strong>Lieu:</strong><br>
                        <span class="ms-4">${salle.localisation}</span>
                    </li>
                    <li class="mb-2">
                        <i class="bi bi-people text-primary"></i>
                        <strong>Capacité:</strong><br>
                        <span class="ms-4">${salle.capacite} personnes</span>
                    </li>
                    <li class="mb-2">
                        <i class="bi bi-currency-exchange text-primary"></i>
                        <strong>Prix:</strong><br>
                        <span class="ms-4 text-success fw-bold">
                            <fmt:formatNumber value="${salle.prixParJour}" type="number" /> FCFA/jour
                        </span>
                    </li>
                </ul>

                <hr>

                <p class="small text-muted mb-2">
                    <i class="bi bi-info-circle"></i>
                    Le montant affiché est le tarif par jour.
                </p>
                <p class="small text-muted mb-0" id="prixCalcule">
                    <strong>Coût total estimé :</strong> <span id="montantTotal"><fmt:formatNumber value="${salle.prixParJour}" type="number" /> FCFA</span>
                </p>
            </div>
        </div>

        <!-- Conseils -->
        <div class="card">
            <div class="card-header bg-warning">
                <h6 class="mb-0"><i class="bi bi-lightbulb"></i> Conseils</h6>
            </div>
            <div class="card-body">
                <ul class="small mb-0">
                    <li>Réservez au moins 3 jours à l'avance</li>
                    <li>Vérifiez la disponibilité avant de réserver</li>
                    <li>Indiquez le nombre exact de personnes</li>
                    <li>Décrivez clairement votre événement</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<script>
    const prixParJour = ${salle.prixParJour};
    
    // Fonction pour calculer le nombre de jours
    function calculateNombreDeJours() {
        const dateDebut = document.getElementById('dateDebut').value;
        const dateFin = document.getElementById('dateFin').value;
        
        if (!dateDebut) return 1;
        if (!dateFin) return 1;
        
        const debut = new Date(dateDebut);
        const fin = new Date(dateFin);
        
        const diffTime = fin - debut;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
        
        return diffDays > 0 ? diffDays : 1;
    }
    
    // Fonction pour mettre à jour le montant total
    function updateMontantTotal() {
        const nombreDeJours = calculateNombreDeJours();
        const montantTotal = prixParJour * nombreDeJours;
        
        document.getElementById('montantTotal').textContent = 
            montantTotal.toLocaleString('fr-FR') + ' FCFA (' + nombreDeJours + ' jour' + (nombreDeJours > 1 ? 's' : '') + ')';
    }
    
    // Écouter les changements de dates
    document.getElementById('dateDebut').addEventListener('change', function() {
        // Mettre à jour le min de dateFin
        document.getElementById('dateFin').min = this.value;
        updateMontantTotal();
    });
    
    document.getElementById('dateFin').addEventListener('change', updateMontantTotal);
    
    // Validation côté client
    document.getElementById('reservationForm').addEventListener('submit', function(e) {
        const dateDebut = document.getElementById('dateDebut').value;
        const dateFin = document.getElementById('dateFin').value;
        const heureDebut = document.getElementById('heureDebut').value;
        const heureFin = document.getElementById('heureFin').value;
        const nombrePersonnes = parseInt(document.getElementById('nombrePersonnes').value);
        const capaciteMax = ${salle.capacite};

        // Vérifier que la date de fin est après ou égale à la date de début
        if (dateFin && dateDebut && dateFin < dateDebut) {
            e.preventDefault();
            alert('La date de fin doit être après ou égale à la date de début');
            return false;
        }

        // Vérifier que l'heure de fin est après l'heure de début
        if (heureFin <= heureDebut) {
            e.preventDefault();
            alert('L\'heure de fin doit être après l\'heure de début');
            return false;
        }

        // Vérifier la capacité
        if (nombrePersonnes > capaciteMax) {
            e.preventDefault();
            alert('Le nombre de personnes dépasse la capacité de la salle (' + capaciteMax + ')');
            return false;
        }
    });
    
    // Initialiser le montant au chargement
    updateMontantTotal();
</script>

<jsp:include page="../common/footer.jsp" />