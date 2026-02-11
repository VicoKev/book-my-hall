<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Dashboard" scope="request"/>
<jsp:include page="../common/header.jsp" />

<!-- Welcome Banner -->
<div class="alert alert-primary alert-dismissible fade show" role="alert">
    <h4 class="alert-heading">
        <i class="bi bi-emoji-smile"></i> Bienvenue, ${user.prenom} ${user.nom} !
    </h4>
    <p class="mb-0">Vous êtes connecté en tant que <strong>${user.username}</strong></p>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>

<!-- Statistics Cards -->
<div class="row g-4 mb-4">
    <div class="col-md-4">
        <div class="card text-center h-100 border-primary">
            <div class="card-body">
                <i class="bi bi-calendar-check text-primary" style="font-size: 3rem;"></i>
                <h3 class="mt-3 mb-0">${totalReservations}</h3>
                <p class="text-muted mb-0">Réservations totales</p>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card text-center h-100 border-success">
            <div class="card-body">
                <i class="bi bi-clock-history text-success" style="font-size: 3rem;"></i>
                <h3 class="mt-3 mb-0">${reservationsEnCours}</h3>
                <p class="text-muted mb-0">Réservations en cours</p>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card text-center h-100 border-warning">
            <div class="card-body">
                <i class="bi bi-building-fill text-warning" style="font-size: 3rem;"></i>
                <h3 class="mt-3 mb-0">
                    <a href="${pageContext.request.contextPath}/user/salles" class="text-decoration-none">
                        Voir les salles
                    </a>
                </h3>
                <p class="text-muted mb-0">Réserver maintenant</p>
            </div>
        </div>
    </div>
</div>

<!-- Quick Actions -->
<div class="card mb-4">
    <div class="card-header bg-primary text-white">
        <h5 class="mb-0"><i class="bi bi-lightning"></i> Actions Rapides</h5>
    </div>
    <div class="card-body">
        <div class="row g-3">
            <div class="col-md-3">
                <a href="${pageContext.request.contextPath}/user/salles" class="btn btn-outline-primary w-100">
                    <i class="bi bi-search"></i><br>Rechercher une salle
                </a>
            </div>
            <div class="col-md-3">
                <a href="${pageContext.request.contextPath}/user/reservations" class="btn btn-outline-success w-100">
                    <i class="bi bi-list-ul"></i><br>Mes réservations
                </a>
            </div>
            <div class="col-md-3">
                <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-info w-100">
                    <i class="bi bi-person"></i><br>Mon profil
                </a>
            </div>
            <div class="col-md-3">
                <a href="${pageContext.request.contextPath}/contact" class="btn btn-outline-warning w-100">
                    <i class="bi bi-envelope"></i><br>Nous contacter
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Upcoming Reservations -->
<div class="card">
    <div class="card-header bg-success text-white">
        <h5 class="mb-0"><i class="bi bi-calendar3"></i> Réservations à Venir</h5>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty reservationsFutures}">
                <div class="text-center py-5 text-muted">
                    <i class="bi bi-calendar-x" style="font-size: 4rem;"></i>
                    <p class="mt-3 mb-0">Aucune réservation à venir</p>
                    <a href="${pageContext.request.contextPath}/user/salles" class="btn btn-primary mt-3">
                        <i class="bi bi-plus-circle"></i> Réserver une salle
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Horaire</th>
                                <th>Salle</th>
                                <th>Événement</th>
                                <th>Statut</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${reservationsFutures}" var="reservation">
                                <tr>
                                    <td>
                                        <i class="bi bi-calendar-event"></i> 
                                        <c:choose>
                                            <c:when test="${not empty reservation.dateFin and reservation.dateFin ne reservation.dateDebut}">
                                                ${reservation.dateDebut} - ${reservation.dateFin}
                                            </c:when>
                                            <c:otherwise>
                                                ${reservation.dateDebut}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <i class="bi bi-clock"></i> 
                                        ${reservation.heureDebut} - ${reservation.heureFin}
                                    </td>
                                    <td>
                                        <i class="bi bi-building-fill"></i> 
                                        ${reservation.salleNom}
                                    </td>
                                    <td>${reservation.typeEvenement}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${reservation.statut == 'PENDING'}">
                                                <span class="badge bg-warning">En attente</span>
                                            </c:when>
                                            <c:when test="${reservation.statut == 'CONFIRMED'}">
                                                <span class="badge bg-success">Confirmée</span>
                                            </c:when>
                                            <c:when test="${reservation.statut == 'CANCELLED'}">
                                                <span class="badge bg-danger">Annulée</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">Terminée</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/reservations/${reservation.id}" 
                                           class="btn btn-sm btn-outline-primary">
                                            <i class="bi bi-eye"></i> Voir
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                
                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/user/reservations" class="btn btn-outline-primary">
                        <i class="bi bi-arrow-right"></i> Voir toutes mes réservations
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />