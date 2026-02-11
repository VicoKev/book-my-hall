<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Tableau de bord" scope="request"/>
<jsp:include page="common/header.jsp" />

<h2 class="mb-4">
    <i class="bi bi-speedometer2"></i> Tableau de bord
</h2>

<div class="row g-4 mb-4">
    <div class="col-md-4">
        <div class="card bg-primary text-white">
            <div class="card-body">
                <h5 class="card-title">
                    <i class="bi bi-calendar-check"></i> Mes Réservations
                </h5>
                <h2 class="mb-0">${reservations.size()}</h2>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card bg-success text-white">
            <div class="card-body">
                <h5 class="card-title">
                    <i class="bi bi-building-fill"></i> Salles Disponibles
                </h5>
                <h2 class="mb-0">${nombreSalles}</h2>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card bg-info text-white">
            <div class="card-body">
                <h5 class="card-title">
                    <i class="bi bi-person-circle"></i> Bienvenue
                </h5>
                <h5 class="mb-0">${user.nomComplet}</h5>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-12">
        <div class="card">
            <div class="card-header bg-white">
                <h5 class="mb-0"><i class="bi bi-calendar-event"></i> Mes Prochaines Réservations</h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty reservations}">
                        <div class="text-center py-5">
                            <i class="bi bi-calendar-x text-muted" style="font-size: 4rem;"></i>
                            <p class="text-muted mt-3">Aucune réservation à venir</p>
                            <a href="${pageContext.request.contextPath}/salles" class="btn btn-primary">
                                <i class="bi bi-search"></i> Réserver une salle
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
                                    <c:forEach items="${reservations}" var="reservation">
                                        <tr>
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
                                            <td>${reservation.heureDebut} - ${reservation.heureFin}</td>
                                            <td>${reservation.salleNom}</td>
                                            <td>${reservation.typeEvenement}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${reservation.statut == 'CONFIRMED'}">
                                                        <span class="badge bg-success">Confirmée</span>
                                                    </c:when>
                                                    <c:when test="${reservation.statut == 'PENDING'}">
                                                        <span class="badge bg-warning">En attente</span>
                                                    </c:when>
                                                    <c:when test="${reservation.statut == 'CANCELLED'}">
                                                        <span class="badge bg-danger">Annulée</span>
                                                    </c:when>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/reservations/cancel/${reservation.id}" 
                                                      method="post" style="display:inline;" 
                                                      onsubmit="return confirm('Êtes-vous sûr de vouloir annuler cette réservation?')">
                                                    <button type="submit" class="btn btn-sm btn-danger" 
                                                            ${reservation.statut == 'CANCELLED' ? 'disabled' : ''}>
                                                        <i class="bi bi-x-circle"></i> Annuler
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp" />