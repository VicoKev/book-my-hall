<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Dashboard Admin" scope="request"/>
<jsp:include page="../common/header.jsp" />

<!-- Welcome Banner -->
<div class="alert alert-danger alert-dismissible fade show" role="alert">
    <h4 class="alert-heading">
        <i class="bi bi-shield-check"></i> Panneau d'Administration
    </h4>
    <p class="mb-0">Bienvenue dans l'espace administrateur de BookMyHall</p>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>

<!-- Statistics Cards -->
<div class="row g-4 mb-4">
    <div class="col-md-3">
        <div class="card text-center h-100 border-primary">
            <div class="card-body">
                <i class="bi bi-people text-primary" style="font-size: 3rem;"></i>
                <h3 class="mt-3 mb-0">${totalUtilisateurs}</h3>
                <p class="text-muted mb-0">Utilisateurs</p>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-sm btn-outline-primary mt-2">
                    Gérer <i class="bi bi-arrow-right"></i>
                </a>
            </div>
        </div>
    </div>
    
    <div class="col-md-3">
        <div class="card text-center h-100 border-success">
            <div class="card-body">
                <i class="bi bi-building-fill text-success" style="font-size: 3rem;"></i>
                <h3 class="mt-3 mb-0">${totalSalles}</h3>
                <p class="text-muted mb-0">Salles totales</p>
                <a href="${pageContext.request.contextPath}/admin/salles" class="btn btn-sm btn-outline-success mt-2">
                    Gérer <i class="bi bi-arrow-right"></i>
                </a>
            </div>
        </div>
    </div>
    
    <div class="col-md-3">
        <div class="card text-center h-100 border-warning">
            <div class="card-body">
                <i class="bi bi-calendar-check text-warning" style="font-size: 3rem;"></i>
                <h3 class="mt-3 mb-0">${totalReservations}</h3>
                <p class="text-muted mb-0">Réservations</p>
                <a href="${pageContext.request.contextPath}/admin/reservations" class="btn btn-sm btn-outline-warning mt-2">
                    Gérer <i class="bi bi-arrow-right"></i>
                </a>
            </div>
        </div>
    </div>
    
    <div class="col-md-3">
        <div class="card text-center h-100 border-info">
            <div class="card-body">
                <i class="bi bi-check-circle text-info" style="font-size: 3rem;"></i>
                <h3 class="mt-3 mb-0">${sallesDisponibles}</h3>
                <p class="text-muted mb-0">Salles disponibles</p>
                <div class="mt-2">
                    <small class="text-muted">${sallesDisponibles}/${totalSalles}</small>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Quick Actions -->
<div class="card mb-4">
    <div class="card-header bg-danger text-white">
        <h5 class="mb-0"><i class="bi bi-lightning"></i> Actions Rapides</h5>
    </div>
    <div class="card-body">
        <div class="row g-3">
            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-primary w-100">
                    <i class="bi bi-person-plus"></i><br>Gérer les utilisateurs
                </a>
            </div>
            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/admin/salles/new" class="btn btn-outline-success w-100">
                    <i class="bi bi-plus-circle"></i><br>Ajouter une salle
                </a>
            </div>
            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/admin/reservations" class="btn btn-outline-warning w-100">
                    <i class="bi bi-calendar-check"></i><br>Voir les réservations
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Recent Reservations -->
<div class="card">
    <div class="card-header bg-primary text-white">
        <h5 class="mb-0"><i class="bi bi-calendar3"></i> Dernières Réservations</h5>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty dernieresReservations}">
                <div class="text-center py-4 text-muted">
                    <i class="bi bi-calendar-x" style="font-size: 3rem;"></i>
                    <p class="mt-2">Aucune réservation pour le moment</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Date</th>
                                <th>Salle</th>
                                <th>Client</th>
                                <th>Statut</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${dernieresReservations}" var="reservation">
                                <tr>
                                    <td>#${reservation.id}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty reservation.dateFin and reservation.dateFin ne reservation.dateDebut}">
                                                ${reservation.dateDebut} - ${reservation.dateFin}
                                            </c:when>
                                            <c:otherwise>
                                                ${reservation.dateDebut}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${reservation.salleNom}</td>
                                    <td>${reservation.utilisateurNom}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${reservation.statut == 'PENDING'}">
                                                <span class="badge bg-warning">En attente</span>
                                            </c:when>
                                            <c:when test="${reservation.statut == 'CONFIRMED'}">
                                                <span class="badge bg-success">Confirmée</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">Annulée</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/reservations/${reservation.id}" 
                                           class="btn btn-sm btn-outline-primary">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/admin/reservations" 
                       class="btn btn-outline-primary">
                        Voir toutes les réservations <i class="bi bi-arrow-right"></i>
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />