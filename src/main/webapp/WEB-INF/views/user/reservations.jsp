<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Mes Réservations" scope="request"/>
<jsp:include page="../common/header.jsp" />

<div class="card">
    <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
        <h5 class="mb-0"><i class="bi bi-calendar-check"></i> Mes Réservations</h5>
        <a href="${pageContext.request.contextPath}/salles" class="btn btn-light btn-sm">
            <i class="bi bi-plus-circle"></i> Nouvelle Réservation
        </a>
    </div>
    <div class="card-body">
        <!-- Filters (Always visible) -->
        <div class="mb-3">
            <div class="btn-group" role="group">
                <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-outline-secondary">
                    <i class="bi bi-speedometer2"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/user/reservations" 
                   class="btn btn-outline-primary ${empty filtreStatut or filtreStatut == 'all' ? 'active' : ''}">
                    Toutes
                </a>
                <a href="${pageContext.request.contextPath}/user/reservations?statut=PENDING" 
                   class="btn btn-outline-warning ${filtreStatut == 'PENDING' ? 'active' : ''}">
                    En attente
                </a>
                <a href="${pageContext.request.contextPath}/user/reservations?statut=CONFIRMED" 
                   class="btn btn-outline-success ${filtreStatut == 'CONFIRMED' ? 'active' : ''}">
                    Confirmées
                </a>
                <a href="${pageContext.request.contextPath}/user/reservations?statut=CANCELLED" 
                   class="btn btn-outline-danger ${filtreStatut == 'CANCELLED' ? 'active' : ''}">
                    Annulées
                </a>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty reservations}">
                <div class="text-center py-5 text-muted border rounded bg-light">
                    <c:choose>
                        <c:when test="${not empty filtreStatut and filtreStatut ne 'all'}">
                            <i class="bi bi-filter-circle-x" style="font-size: 5rem;"></i>
                            <h4 class="mt-3">Aucun résultat</h4>
                            <p>Aucune réservation avec le statut <strong>${filtreLibelle}</strong>.</p>
                            <a href="${pageContext.request.contextPath}/user/reservations" class="btn btn-outline-primary mt-3">
                                <i class="bi bi-arrow-left"></i> Voir toutes mes réservations
                            </a>
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-calendar-x" style="font-size: 5rem;"></i>
                            <h4 class="mt-3">Aucune réservation</h4>
                            <p>Vous n'avez pas encore effectué de réservation.</p>
                            <a href="${pageContext.request.contextPath}/user/salles" class="btn btn-primary mt-3">
                                <i class="bi bi-search"></i> Parcourir les salles disponibles
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Reservations List -->
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>#</th>
                                <th>Date</th>
                                <th>Horaire</th>
                                <th>Salle</th>
                                <th>Événement</th>
                                <th>Personnes</th>
                                <th>Montant</th>
                                <th>Statut</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${reservations}" var="reservation" varStatus="status">
                                <tr>
                                    <td>${(currentPage * 10) + status.count}</td>
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
                                        <strong>${reservation.salleNom}</strong><br>
                                        <small class="text-muted">Capacité: ${reservation.salleCapacite} pers.</small>
                                    </td>
                                    <td>${reservation.typeEvenement}</td>
                                    <td>${reservation.nombrePersonnes}</td>
                                    <td>
                                        <strong>
                                            <fmt:formatNumber value="${reservation.montantTotal}" type="number" /> FCFA
                                        </strong>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${reservation.statut == 'PENDING'}">
                                                <span class="badge bg-warning">
                                                    <i class="bi bi-hourglass-split"></i> En attente
                                                </span>
                                            </c:when>
                                            <c:when test="${reservation.statut == 'CONFIRMED'}">
                                                <span class="badge bg-success">
                                                    <i class="bi bi-check-circle"></i> Confirmée
                                                </span>
                                            </c:when>
                                            <c:when test="${reservation.statut == 'CANCELLED'}">
                                                <span class="badge bg-danger">
                                                    <i class="bi bi-x-circle"></i> Annulée
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">
                                                    <i class="bi bi-check-all"></i> Terminée
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="btn-group btn-group-sm gap-1">
                                            <a href="${pageContext.request.contextPath}/reservations/${reservation.id}" 
                                               class="btn btn-outline-primary" title="Voir détails">
                                                <i class="bi bi-eye"></i>
                                            </a>
                                            <c:if test="${reservation.statut == 'PENDING' || reservation.statut == 'CONFIRMED'}">
                                                <form action="${pageContext.request.contextPath}/reservations/${reservation.id}/annuler" 
                                                      method="post" style="display:inline;"
                                                      onsubmit="return confirm('Êtes-vous sûr de vouloir annuler cette réservation ?');">
                                                    <button type="submit" class="btn btn-outline-danger" title="Annuler">
                                                        <i class="bi bi-x-circle"></i>
                                                    </button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <c:set var="pageObj" value="${reservationsPage}" scope="request" />
                <jsp:include page="../common/pagination.jsp">
                    <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/user/reservations" />
                    <jsp:param name="queryParams" value="${not empty filtreStatut ? '&statut='.concat(filtreStatut) : ''}" />
                </jsp:include>
            </c:otherwise>
        </c:choose>
    </div>
</div>



<jsp:include page="../common/footer.jsp" />