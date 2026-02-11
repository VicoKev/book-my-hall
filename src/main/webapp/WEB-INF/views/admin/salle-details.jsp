<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Administration - ${salle.nom}" scope="request"/>
<jsp:include page="../common/header.jsp" />

<!-- Breadcrumb -->
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
        </li>
        <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/admin/salles">Salles</a>
        </li>
        <li class="breadcrumb-item active">${salle.nom}</li>
    </ol>
</nav>

<div class="row">
    <!-- Main Content -->
    <div class="col-lg-8">
        <!-- Image principale -->
        <div class="card mb-4">
            <div class="card-body text-center bg-light" style="min-height: 400px;">
                <c:choose>
                    <c:when test="${not empty salle.imageFileName}">
                        <img src="${pageContext.request.contextPath}/images/salles/${salle.imageFileName}" alt="${salle.nom}"
                             class="img-fluid rounded" style="max-height: 400px;">
                    </c:when>
                    <c:otherwise>
                        <div class="d-flex align-items-center justify-content-center h-100">
                            <i class="bi bi-building-fill text-muted" style="font-size: 8rem;"></i>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Informations détaillées -->
        <div class="card mb-4">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0"><i class="bi bi-info-circle"></i> Description</h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty salle.description}">
                        <p class="lead">${salle.description}</p>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted">Aucune description disponible</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Équipements -->
        <div class="card mb-4">
            <div class="card-header bg-success text-white">
                <h5 class="mb-0"><i class="bi bi-tools"></i> Équipements</h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty salle.equipements}">
                        <div class="row">
                            <c:set var="equipementsList" value="${fn:split(salle.equipements, ',')}" />
                            <c:forEach items="${equipementsList}" var="equipement">
                                <div class="col-md-6 mb-2">
                                    <i class="bi bi-check-circle text-success"></i> 
                                    ${fn:trim(equipement)}
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted">Aucun équipement spécifié</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Sidebar -->
    <div class="col-lg-4">
        <!-- Actions Admin -->
        <div class="card mb-4 border-primary shadow-sm">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0"><i class="bi bi-gear-fill"></i> Administration</h5>
            </div>
            <div class="card-body">
                <div class="d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/admin/salles/${salle.id}/edit" 
                       class="btn btn-primary">
                        <i class="bi bi-pencil"></i> Modifier la salle
                    </a>
                    
                    <form action="${pageContext.request.contextPath}/admin/salles/${salle.id}/toggle-disponibilite" 
                          method="post" class="d-grid">
                        <c:choose>
                            <c:when test="${salle.disponible}">
                                <input type="hidden" name="disponible" value="false">
                                <button type="submit" class="btn btn-outline-warning">
                                    <i class="bi bi-x-circle"></i> Marquer indisponible
                                </button>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="disponible" value="true">
                                <button type="submit" class="btn btn-outline-success">
                                    <i class="bi bi-check-circle"></i> Marquer disponible
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </form>
                    
                    <hr>
                    
                    <form action="${pageContext.request.contextPath}/admin/salles/${salle.id}/delete" 
                          method="post" class="d-grid"
                          onsubmit="return confirm('Attention: Supprimer cette salle est irréversible. Continuer ?');">
                        <button type="submit" class="btn btn-danger">
                            <i class="bi bi-trash"></i> Supprimer la salle
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <!-- Statistiques et Infos -->
        <div class="card mb-4 shadow-sm">
            <div class="card-body">
                <ul class="list-unstyled mb-0">
                    <li class="mb-2">
                        <i class="bi bi-cash text-primary"></i>
                        <strong>Prix/Jour:</strong> <fmt:formatNumber value="${salle.prixParJour}" type="number" /> FCFA
                    </li>
                    <li class="mb-2">
                        <i class="bi bi-people text-primary"></i>
                        <strong>Capacité:</strong> ${salle.capacite} pers.
                    </li>
                    <li class="mb-2">
                        <i class="bi bi-geo-alt text-primary"></i>
                        <strong>Localisation:</strong> ${salle.localisation}
                    </li>
                    <li class="mb-0">
                        <i class="bi bi-calendar-event text-primary"></i>
                        <strong>Réservations:</strong> ${salle.nombreReservations}
                    </li>
                </ul>
            </div>
        </div>

        <div class="d-grid">
            <a href="${pageContext.request.contextPath}/admin/salles" 
               class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Retour à la liste
            </a>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
