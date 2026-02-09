<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Gestion des Salles" scope="request"/>
<jsp:include page="../common/header.jsp" />

<!-- Page Header -->
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2><i class="bi bi-building"></i> Gestion des Salles</h2>
        <p class="text-muted mb-0">Total: ${sallesPage.totalElements} salle(s)</p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/admin/salles/new" class="btn btn-success">
            <i class="bi bi-plus-circle"></i> Nouvelle Salle
        </a>
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left"></i> Retour
        </a>
    </div>
</div>

<!-- Salles Table -->
<div class="card mb-4">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Nom</th>
                        <th>Localisation</th>
                        <th>Capacité</th>
                        <th>Prix/Jour</th>
                        <th>Disponible</th>
                        <th>Réservations</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${salles}" var="salle">
                        <tr>
                            <td>#${salle.id}</td>
                            <td><strong>${salle.nom}</strong></td>
                            <td>${salle.localisation}</td>
                            <td>
                                <i class="bi bi-people"></i> ${salle.capacite}
                            </td>
                            <td>
                                <fmt:formatNumber value="${salle.prixParJour}" type="number" /> FCFA
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${salle.disponible}">
                                        <span class="badge bg-success">Oui</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger">Non</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${salle.nombreReservations}</td>
                            <td>
                                <div class="btn-group btn-group-sm gap-1">
                                    <!-- Voir -->
                                    <a href="${pageContext.request.contextPath}/salles/${salle.id}" 
                                       class="btn btn-outline-info" title="Voir">
                                        <i class="bi bi-eye"></i>
                                    </a>
                                    
                                    <!-- Éditer -->
                                    <a href="${pageContext.request.contextPath}/admin/salles/${salle.id}/edit" 
                                       class="btn btn-outline-primary" title="Éditer">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    
                                    <!-- Toggle Disponibilité -->
                                    <form action="${pageContext.request.contextPath}/admin/salles/${salle.id}/toggle-disponibilite" 
                                          method="post" style="display:inline;">
                                        <c:choose>
                                            <c:when test="${salle.disponible}">
                                                <input type="hidden" name="disponible" value="false">
                                                <button type="submit" class="btn btn-outline-warning" 
                                                        title="Marquer indisponible">
                                                    <i class="bi bi-x-circle"></i>
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="hidden" name="disponible" value="true">
                                                <button type="submit" class="btn btn-outline-success" 
                                                        title="Marquer disponible">
                                                    <i class="bi bi-check-circle"></i>
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                    
                                    <!-- Supprimer -->
                                    <form action="${pageContext.request.contextPath}/admin/salles/${salle.id}/delete" 
                                          method="post" style="display:inline;"
                                          onsubmit="return confirm('Supprimer cette salle ? (Impossible si elle a des réservations)');">
                                        <button type="submit" class="btn btn-outline-danger" title="Supprimer">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty salles}">
                        <tr>
                            <td colspan="8" class="text-center py-4">
                                <div class="text-muted">
                                    <i class="bi bi-inbox fs-1 d-block mb-2"></i>
                                    Aucune salle trouvée
                                </div>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
        
        <!-- Pagination -->
        <c:set var="pageObj" value="${sallesPage}" scope="request" />
        <jsp:include page="../common/pagination.jsp">
            <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/admin/salles" />
        </jsp:include>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />