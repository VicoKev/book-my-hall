<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Gestion des Réservations" scope="request"/>
<jsp:include page="../common/header.jsp" />

<!-- Page Header -->
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2><i class="bi bi-calendar-check"></i> Gestion des Réservations</h2>
        <p class="text-muted mb-0">Total: ${reservationsPage.totalElements} réservation(s)</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Retour
    </a>
</div>

<!-- Filters -->
<div class="mb-3">
    <div class="btn-group" role="group">
        <a href="${pageContext.request.contextPath}/admin/reservations" 
           class="btn btn-outline-primary ${empty filtreStatut ? 'active' : ''}">
            Toutes
        </a>
        <a href="${pageContext.request.contextPath}/admin/reservations?statut=PENDING" 
           class="btn btn-outline-warning ${filtreStatut == 'PENDING' ? 'active' : ''}">
            En attente
        </a>
        <a href="${pageContext.request.contextPath}/admin/reservations?statut=CONFIRMED" 
           class="btn btn-outline-success ${filtreStatut == 'CONFIRMED' ? 'active' : ''}">
            Confirmées
        </a>
        <a href="${pageContext.request.contextPath}/admin/reservations?statut=CANCELLED" 
           class="btn btn-outline-danger ${filtreStatut == 'CANCELLED' ? 'active' : ''}">
            Annulées
        </a>
    </div>
</div>

<!-- Reservations Table -->
<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Date</th>
                        <th>Horaire</th>
                        <th>Salle</th>
                        <th>Client</th>
                        <th>Événement</th>
                        <th>Montant</th>
                        <th>Statut</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${reservations}" var="reservation">
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
                            <td>
                                ${reservation.heureDebut} - ${reservation.heureFin}
                            </td>
                            <td>${reservation.salleNom}</td>
                            <td>${reservation.utilisateurNom}</td>
                            <td>${reservation.typeEvenement}</td>
                            <td>
                                <fmt:formatNumber value="${reservation.montantTotal}" type="number" /> FCFA
                            </td>
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
                                <div class="btn-group btn-group-sm gap-1">
                                    <!-- Voir détails -->
                                    <a href="${pageContext.request.contextPath}/admin/reservations/${reservation.id}" 
                                       class="btn btn-outline-primary" title="Details">
                                        <i class="bi bi-eye"></i>
                                    </a>
                                    
                                    <!-- Confirmer si PENDING -->
                                    <c:if test="${reservation.statut == 'PENDING'}">
                                        <form action="${pageContext.request.contextPath}/admin/reservations/${reservation.id}/confirmer" 
                                              method="post" style="display:inline;">
                                            <button type="submit" class="btn btn-outline-success" 
                                                    title="Confirmer">
                                                <i class="bi bi-check-circle"></i>
                                            </button>
                                        </form>
                                    </c:if>
                                    
                                    <!-- Annuler si PENDING ou CONFIRMED -->
                                    <c:if test="${reservation.statut == 'PENDING' || reservation.statut == 'CONFIRMED'}">
                                        <form action="${pageContext.request.contextPath}/admin/reservations/${reservation.id}/annuler" 
                                              method="post" style="display:inline;"
                                              onsubmit="return confirm('Annuler cette réservation ?');">
                                            <button type="submit" class="btn btn-outline-danger" 
                                                    title="Annuler">
                                                <i class="bi bi-x-circle"></i>
                                            </button>
                                        </form>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty reservations}">
                        <tr>
                            <td colspan="9" class="text-center py-4">
                                <div class="text-muted">
                                    <i class="bi bi-calendar-x fs-1 d-block mb-2"></i>
                                    <c:choose>
                                        <c:when test="${not empty filtreLibelle}">
                                            Aucune réservation avec le statut <strong>${filtreLibelle}</strong>
                                        </c:when>
                                        <c:otherwise>
                                            Aucune réservation trouvée
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
        
        <!-- Pagination -->
        <c:set var="pageObj" value="${reservationsPage}" scope="request" />
        <jsp:include page="../common/pagination.jsp">
            <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/admin/reservations" />
            <jsp:param name="queryParams" value="${not empty filtreStatut ? '&statut='.concat(filtreStatut) : ''}" />
        </jsp:include>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />