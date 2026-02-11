<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="${salle.nom}" scope="request"/>
<jsp:include page="../common/header.jsp" />

<!-- Breadcrumb -->
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/user/salles">Salles</a>
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

        <!-- Statistiques -->
        <c:if test="${salle.nombreReservations > 0}">
            <div class="alert alert-info">
                <i class="bi bi-star-fill text-warning"></i>
                <strong>Populaire!</strong> Cette salle a été réservée 
                <strong>${salle.nombreReservations}</strong> fois
            </div>
        </c:if>
    </div>

    <!-- Sidebar -->
    <div class="col-lg-4">
        <!-- Informations principales -->
        <div class="card mb-4 shadow">
            <div class="card-body">
                <h3 class="card-title">${salle.nom}</h3>
                
                <hr>
                
                <div class="mb-3">
                    <h4 class="text-primary">
                        <fmt:formatNumber value="${salle.prixParJour}" type="number" /> FCFA
                        <small class="text-muted">/jour</small>
                    </h4>
                </div>
                
                <ul class="list-unstyled">
                    <li class="mb-2">
                        <i class="bi bi-geo-alt text-primary"></i>
                        <strong>Localisation:</strong><br>
                        <span class="ms-4">${salle.localisation}</span>
                    </li>
                    <li class="mb-2">
                        <i class="bi bi-people text-primary"></i>
                        <strong>Capacité:</strong><br>
                        <span class="ms-4">${salle.capacite} personnes</span>
                    </li>
                    <li class="mb-2">
                        <i class="bi bi-check-circle text-primary"></i>
                        <strong>Disponibilité:</strong><br>
                        <span class="ms-4">
                            <c:choose>
                                <c:when test="${salle.disponible}">
                                    <span class="badge bg-success">Disponible</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger">Indisponible</span>
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </li>
                </ul>
                
                <hr>
                
                <div class="d-grid gap-2">
                    <c:choose>
                        <c:when test="${salle.disponible}">
                            <a href="${pageContext.request.contextPath}/user/reservations/new?salleId=${salle.id}" 
                               class="btn btn-primary btn-lg">
                                <i class="bi bi-calendar-check"></i> Réserver cette salle
                            </a>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-secondary btn-lg" disabled>
                                <i class="bi bi-x-circle"></i> Salle indisponible
                            </button>
                        </c:otherwise>
                    </c:choose>
                    
                    <a href="${pageContext.request.contextPath}/user/salles" 
                       class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left"></i> Retour à la liste
                    </a>
                </div>
            </div>
        </div>

        <!-- Contact -->
        <div class="card">
            <div class="card-header bg-info text-white">
                <h6 class="mb-0"><i class="bi bi-question-circle"></i> Besoin d'aide ?</h6>
            </div>
            <div class="card-body">
                <p class="small mb-2">
                    Pour toute question sur cette salle ou pour une demande spéciale, 
                    n'hésitez pas à nous contacter.
                </p>
                <a href="${pageContext.request.contextPath}/contact" class="btn btn-sm btn-outline-info w-100">
                    <i class="bi bi-envelope"></i> Nous contacter
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />