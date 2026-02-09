<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Détails Réservation" scope="request"/>
<jsp:include page="../common/header.jsp" />

<!-- Breadcrumb -->
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
        </li>
        <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/user/reservations">Mes Réservations</a>
        </li>
        <li class="breadcrumb-item active">Réservation #${reservation.id}</li>
    </ol>
</nav>

<!-- Statut -->
<div class="alert 
    <c:choose>
        <c:when test='${reservation.statut == "PENDING"}'>alert-warning</c:when>
        <c:when test='${reservation.statut == "CONFIRMED"}'>alert-success</c:when>
        <c:when test='${reservation.statut == "CANCELLED"}'>alert-danger</c:when>
        <c:otherwise>alert-secondary</c:otherwise>
    </c:choose>
">
    <div class="row align-items-center">
        <div class="col-md-8">
            <h5 class="alert-heading mb-0">
                <c:choose>
                    <c:when test="${reservation.statut == 'PENDING'}">
                        <i class="bi bi-hourglass-split"></i> Réservation en attente de confirmation
                    </c:when>
                    <c:when test="${reservation.statut == 'CONFIRMED'}">
                        <i class="bi bi-check-circle"></i> Réservation confirmée
                    </c:when>
                    <c:when test="${reservation.statut == 'CANCELLED'}">
                        <i class="bi bi-x-circle"></i> Réservation annulée
                    </c:when>
                    <c:otherwise>
                        <i class="bi bi-check-all"></i> Réservation terminée
                    </c:otherwise>
                </c:choose>
            </h5>
        </div>
        <div class="col-md-4 text-end">
            <!-- Actions selon le statut -->
            <c:choose>
                <c:when test="${reservation.statut == 'PENDING'}">
                    <c:if test="${currentUser.role == 'ADMIN'}">
                        <form action="${pageContext.request.contextPath}/reservations/${reservation.id}/confirmer" 
                              method="post" style="display:inline;">
                            <button type="submit" class="btn btn-success btn-sm">
                                <i class="bi bi-check-circle"></i> Confirmer
                            </button>
                        </form>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/reservations/${reservation.id}/annuler" 
                          method="post" style="display:inline;" 
                          onsubmit="return confirm('Êtes-vous sûr de vouloir annuler cette réservation ?');">
                        <button type="submit" class="btn btn-danger btn-sm">
                            <i class="bi bi-x-circle"></i> Annuler
                        </button>
                    </form>
                </c:when>
                <c:when test="${reservation.statut == 'CONFIRMED'}">
                    <form action="${pageContext.request.contextPath}/reservations/${reservation.id}/annuler" 
                          method="post" style="display:inline;"
                          onsubmit="return confirm('Êtes-vous sûr de vouloir annuler cette réservation confirmée ?');">
                        <button type="submit" class="btn btn-warning btn-sm">
                            <i class="bi bi-x-circle"></i> Annuler
                        </button>
                    </form>
                </c:when>
            </c:choose>
        </div>
    </div>
</div>

<div class="row">
    <!-- Détails principaux -->
    <div class="col-lg-8">
        <div class="card mb-4">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0"><i class="bi bi-calendar-check"></i> Informations sur la Réservation</h5>
            </div>
            <div class="card-body">
                <table class="table table-borderless">
                    <tbody>
                        <tr>
                            <th width="30%"><i class="bi bi-hash"></i> Numéro:</th>
                            <td><strong>#${reservation.id}</strong></td>
                        </tr>
                        <tr>
                            <th><i class="bi bi-calendar-event"></i> Date:</th>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty reservation.dateFin and reservation.dateFin ne reservation.dateDebut}">
                                        Du ${reservation.dateDebut} au ${reservation.dateFin}
                                    </c:when>
                                    <c:otherwise>
                                        ${reservation.dateDebut}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th><i class="bi bi-clock"></i> Horaires:</th>
                            <td>
                                De <strong>${reservation.heureDebut}</strong> 
                                à <strong>${reservation.heureFin}</strong>
                                <span class="text-muted">
                                    (${reservation.getDureeEnHeures()} heure(s))
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <th><i class="bi bi-tag"></i> Type d'événement:</th>
                            <td>${reservation.typeEvenement}</td>
                        </tr>
                        <tr>
                            <th><i class="bi bi-people"></i> Nombre de personnes:</th>
                            <td>${reservation.nombrePersonnes}</td>
                        </tr>
                        <tr>
                            <th><i class="bi bi-person"></i> Réservé par:</th>
                            <td>${reservation.utilisateurNom}</td>
                        </tr>
                    </tbody>
                </table>

                <c:if test="${not empty reservation.description}">
                    <hr>
                    <h6><i class="bi bi-card-text"></i> Description:</h6>
                    <p class="text-muted">${reservation.description}</p>
                </c:if>
            </div>
        </div>

        <!-- Informations sur la salle -->
        <div class="card">
            <div class="card-header bg-success text-white">
                <h5 class="mb-0"><i class="bi bi-building"></i> Informations sur la Salle</h5>
            </div>
            <div class="card-body">
                <h5>${reservation.salleNom}</h5>
                <p class="mb-2">
                    <i class="bi bi-people"></i> Capacité: ${reservation.salleCapacite} personnes
                </p>
                <a href="${pageContext.request.contextPath}/salles/${reservation.salleId}" 
                   class="btn btn-outline-primary btn-sm">
                    <i class="bi bi-eye"></i> Voir la salle
                </a>
            </div>
        </div>
    </div>

    <!-- Sidebar -->
    <div class="col-lg-4">
        <!-- Prix -->
        <div class="card mb-4">
            <div class="card-header bg-warning">
                <h6 class="mb-0"><i class="bi bi-currency-exchange"></i> Montant</h6>
            </div>
            <div class="card-body text-center">
                <h2 class="text-success mb-0">
                    <fmt:formatNumber value="${reservation.montantTotal}" type="number" /> FCFA
                </h2>
                <p class="text-muted small mb-0">Montant total</p>
            </div>
        </div>

        <!-- Statut détaillé -->
        <div class="card mb-4">
            <div class="card-header bg-info text-white">
                <h6 class="mb-0"><i class="bi bi-info-circle"></i> Statut</h6>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${reservation.statut == 'PENDING'}">
                        <span class="badge bg-warning fs-6 mb-2">En attente</span>
                        <p class="small mb-0">
                            Votre réservation est en attente de confirmation par un administrateur.
                        </p>
                    </c:when>
                    <c:when test="${reservation.statut == 'CONFIRMED'}">
                        <span class="badge bg-success fs-6 mb-2">Confirmée</span>
                        <p class="small mb-0">
                            Votre réservation a été confirmée. La salle vous est réservée !
                        </p>
                    </c:when>
                    <c:when test="${reservation.statut == 'CANCELLED'}">
                        <span class="badge bg-danger fs-6 mb-2">Annulée</span>
                        <p class="small mb-0">
                            Cette réservation a été annulée.
                        </p>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-secondary fs-6 mb-2">Terminée</span>
                        <p class="small mb-0">
                            L'événement est passé. Merci d'avoir choisi BookMyHall !
                        </p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Actions -->
        <div class="card">
            <div class="card-header">
                <h6 class="mb-0"><i class="bi bi-gear"></i> Actions</h6>
            </div>
            <div class="card-body">
                <div class="d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/user/reservations" 
                       class="btn btn-outline-primary">
                        <i class="bi bi-list"></i> Mes réservations
                    </a>
                    <a href="${pageContext.request.contextPath}/salles" 
                       class="btn btn-outline-secondary">
                        <i class="bi bi-search"></i> Voir les salles
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />