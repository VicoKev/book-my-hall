<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Mes Réservations" scope="request"/>
<jsp:include page="../common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2><i class="bi bi-calendar-check"></i> Mes Réservations</h2>
    <a href="${pageContext.request.contextPath}/salles" class="btn btn-primary">
        <i class="bi bi-plus-circle"></i> Nouvelle Réservation
    </a>
</div>

<c:choose>
    <c:when test="${empty reservations}">
        <div class="card">
            <div class="card-body text-center py-5">
                <i class="bi bi-calendar-x text-muted" style="font-size: 5rem;"></i>
                <h4 class="mt-3">Aucune réservation</h4>
                <p class="text-muted">Vous n'avez pas encore effectué de réservation</p>
                <a href="${pageContext.request.contextPath}/salles" class="btn btn-primary">
                    <i class="bi bi-search"></i> Explorer les salles
                </a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-4">
            <c:forEach items="${reservations}" var="reservation">
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header bg-white d-flex justify-content-between align-items-center">
                            <h5 class="mb-0">${reservation.salleNom}</h5>
                            <c:choose>
                                <c:when test="${reservation.statut == 'CONFIRMED'}">
                                    <span class="badge bg-success">Confirmée</span>
                                </c:when>
                                <c:when test="${reservation.statut == 'PENDING'}">
                                    <span class="badge bg-warning text-dark">En attente</span>
                                </c:when>
                                <c:when test="${reservation.statut == 'CANCELLED'}">
                                    <span class="badge bg-danger">Annulée</span>
                                </c:when>
                                <c:when test="${reservation.statut == 'COMPLETED'}">
                                    <span class="badge bg-secondary">Terminée</span>
                                </c:when>
                            </c:choose>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-6">
                                    <p class="mb-2">
                                        <i class="bi bi-calendar-event text-primary"></i>
                                        <strong>Date:</strong><br>
                                        <c:choose>
                                            <c:when test="${not empty reservation.dateFin and reservation.dateFin ne reservation.dateDebut}">
                                                ${reservation.dateDebut} - ${reservation.dateFin}
                                            </c:when>
                                            <c:otherwise>
                                                ${reservation.dateDebut}
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                </div>
                                <div class="col-6">
                                    <p class="mb-2">
                                        <i class="bi bi-clock text-primary"></i>
                                        <strong>Horaire:</strong><br>
                                        ${reservation.heureDebut} - ${reservation.heureFin}
                                    </p>
                                </div>
                            </div>
                            
                            <p class="mb-2">
                                <i class="bi bi-tag text-primary"></i>
                                <strong>Type d'événement:</strong> ${reservation.typeEvenement}
                            </p>
                            
                            <p class="mb-2">
                                <i class="bi bi-people text-primary"></i>
                                <strong>Nombre de personnes:</strong> ${reservation.nombrePersonnes}
                            </p>
                            
                            <c:if test="${not empty reservation.description}">
                                <p class="mb-2">
                                    <i class="bi bi-file-text text-primary"></i>
                                    <strong>Description:</strong><br>
                                    ${reservation.description}
                                </p>
                            </c:if>
                            
                            <c:if test="${not empty reservation.montantTotal}">
                                <p class="mb-0 text-end">
                                    <strong class="text-primary">Montant:</strong> 
                                    <span class="fs-5 fw-bold">
                                        <fmt:formatNumber value="${reservation.montantTotal}" type="number"/> FCFA
                                    </span>
                                </p>
                            </c:if>
                        </div>
                        <div class="card-footer bg-white d-flex gap-2">
                            <c:if test="${reservation.statut == 'PENDING'}">
                                <form action="${pageContext.request.contextPath}/reservations/confirm/${reservation.id}" 
                                      method="post" class="flex-grow-1">
                                    <button type="submit" class="btn btn-success w-100">
                                        <i class="bi bi-check-circle"></i> Confirmer
                                    </button>
                                </form>
                            </c:if>
                            
                            <c:if test="${reservation.statut == 'PENDING' || reservation.statut == 'CONFIRMED'}">
                                <form action="${pageContext.request.contextPath}/reservations/cancel/${reservation.id}" 
                                      method="post" class="flex-grow-1" 
                                      onsubmit="return confirm('Êtes-vous sûr de vouloir annuler cette réservation?')">
                                    <button type="submit" class="btn btn-danger w-100">
                                        <i class="bi bi-x-circle"></i> Annuler
                                    </button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="../common/footer.jsp" />