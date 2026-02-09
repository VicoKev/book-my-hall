<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Nouvelle Réservation" scope="request"/>
<jsp:include page="../common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header bg-white">
                <h4 class="mb-0"><i class="bi bi-calendar-plus"></i> Nouvelle Réservation</h4>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/reservations/add" method="post">
                    
                    <div class="mb-3">
                        <label for="salleId" class="form-label">Salle *</label>
                        <select class="form-select" id="salleId" name="salleId" required>
                            <option value="">Sélectionnez une salle</option>
                            <c:forEach items="${salles}" var="salle">
                                <option value="${salle.id}" ${reservationDTO.salleId == salle.id ? 'selected' : ''}>
                                    ${salle.nom} - ${salle.localisation} (${salle.capacite} pers.)
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="dateDebut" class="form-label">Date de début *</label>
                            <input type="date" class="form-control" id="dateDebut" name="dateDebut" 
                                   value="${reservationDTO.dateDebut}" required 
                                   min="<%= java.time.LocalDate.now().plusDays(1) %>">
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="dateFin" class="form-label">Date de fin</label>
                            <input type="date" class="form-control" id="dateFin" name="dateFin" 
                                   value="${reservationDTO.dateFin}"
                                   min="<%= java.time.LocalDate.now().plusDays(1) %>">
                            <small class="text-muted">Optionnel - Laissez vide pour un seul jour</small>
                        </div>
                    </div>
                    
                    <div class="row">
                            <label for="heureDebut" class="form-label">Heure de début *</label>
                            <input type="time" class="form-control" id="heureDebut" name="heureDebut" 
                                   value="${reservationDTO.heureDebut}" required>
                        </div>
                        
                        <div class="col-md-4 mb-3">
                            <label for="heureFin" class="form-label">Heure de fin *</label>
                            <input type="time" class="form-control" id="heureFin" name="heureFin" 
                                   value="${reservationDTO.heureFin}" required>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="typeEvenement" class="form-label">Type d'événement *</label>
                        <input type="text" class="form-control" id="typeEvenement" name="typeEvenement" 
                               value="${reservationDTO.typeEvenement}" 
                               placeholder="Ex: Mariage, Anniversaire, Conférence..." required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="nombrePersonnes" class="form-label">Nombre de personnes *</label>
                        <input type="number" class="form-control" id="nombrePersonnes" name="nombrePersonnes" 
                               value="${reservationDTO.nombrePersonnes}" min="1" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="description" class="form-label">Description / Notes supplémentaires</label>
                        <textarea class="form-control" id="description" name="description" rows="3" 
                                  placeholder="Ajoutez des détails sur votre événement...">${reservationDTO.description}</textarea>
                    </div>
                    
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle"></i>
                        <strong>Information:</strong> Votre réservation sera initialement en statut "En attente" 
                        jusqu'à confirmation par l'administrateur.
                    </div>
                    
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-check-circle"></i> Réserver
                        </button>
                        <a href="${pageContext.request.contextPath}/salles" class="btn btn-secondary">
                            <i class="bi bi-x-circle"></i> Annuler
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />