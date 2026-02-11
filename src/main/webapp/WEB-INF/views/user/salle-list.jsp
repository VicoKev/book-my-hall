<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Nos Salles" scope="request"/>
<jsp:include page="../common/header.jsp" />

<!-- Page Header -->
<div class="row mb-4">
    <div class="col-md-8">
        <h2 class="mb-0">
            <i class="bi bi-building-fill"></i> Nos Salles de Réception
        </h2>
        <p class="text-muted">Trouvez la salle parfaite pour votre événement</p>
    </div>
    <div class="col-md-4 text-end">
        <button class="btn btn-primary" data-bs-toggle="collapse" data-bs-target="#filterPanel">
            <i class="bi bi-funnel"></i> Filtres
        </button>
    </div>
</div>

<!-- Filter Panel -->
<div class="collapse mb-4 <c:if test='${hasFilters}'>show</c:if>" id="filterPanel">
    <div class="card">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/user/salles" method="get">
                <div class="row g-3">
                    <div class="col-md-4">
                        <label for="localisation" class="form-label">
                            <i class="bi bi-geo-alt"></i> Localisation
                        </label>
                        <input type="text" class="form-control" id="localisation" 
                               name="localisation" value="${localisation}" 
                               placeholder="Ex: Cotonou, Porto-Novo...">
                    </div>
                    
                    <div class="col-md-4">
                        <label for="capaciteMin" class="form-label">
                            <i class="bi bi-people"></i> Capacité minimale
                        </label>
                        <input type="number" class="form-control" id="capaciteMin" 
                               name="capaciteMin" value="${capaciteMin}" 
                               min="10" placeholder="Nombre de personnes">
                    </div>
                    
                    <div class="col-md-4">
                        <label for="prixMax" class="form-label">
                            <i class="bi bi-currency-exchange"></i> Prix maximum (FCFA)
                        </label>
                        <input type="number" class="form-control" id="prixMax" 
                               name="prixMax" value="${prixMax}" 
                               min="0" step="10000" placeholder="Budget maximum">
                    </div>
                </div>
                
                <div class="row mt-3">
                    <div class="col-12 text-end">
                        <a href="${pageContext.request.contextPath}/user/salles" class="btn btn-outline-secondary">
                            <i class="bi bi-x-circle"></i> Réinitialiser
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-search"></i> Rechercher
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Results Info -->
<c:if test="${hasFilters}">
    <div class="alert alert-info alert-dismissible fade show">
        <i class="bi bi-info-circle"></i> 
        <strong>${sallesPage.totalElements}</strong> salle(s) trouvée(s) avec vos critères
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<!-- Salles List -->
<div class="mb-4">
    <c:choose>
        <c:when test="${empty salles}">
            <div class="text-center py-5">
                <i class="bi bi-building-fill-x text-muted" style="font-size: 5rem;"></i>
                <h3 class="mt-3">Aucune salle trouvée</h3>
                <p class="text-muted">Essayez de modifier vos critères de recherche</p>
                <a href="${pageContext.request.contextPath}/user/salles" class="btn btn-primary mt-3">
                    <i class="bi bi-arrow-counterclockwise"></i> Voir toutes les salles
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row g-4">
                <c:forEach items="${salles}" var="salle">
                    <div class="col-md-6 col-lg-4">
                        <div class="card h-100 shadow-sm hover-shadow">
                            <!-- Image placeholder -->
                            <div class="card-img-top bg-light d-flex align-items-center justify-content-center" 
                                style="height: 200px;">
                                <c:choose>
                                    <c:when test="${not empty salle.imageFileName}">
                                        <img src="${pageContext.request.contextPath}/images/salles/${salle.imageFileName}" alt="${salle.nom}"
                                            class="img-fluid" style="max-height: 200px;">
                                    </c:when>
                                    <c:otherwise>
                                        <i class="bi bi-building-fill text-muted" style="font-size: 4rem;"></i>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <div class="card-body">
                                <h5 class="card-title">${salle.nom}</h5>
                                
                                <div class="mb-2">
                                    <small class="text-muted">
                                        <i class="bi bi-geo-alt"></i> ${salle.localisation}
                                    </small>
                                </div>
                                
                                <div class="mb-2">
                                    <span class="badge bg-primary">
                                        <i class="bi bi-people"></i> ${salle.capacite} personnes
                                    </span>
                                    <c:if test="${salle.disponible}">
                                        <span class="badge bg-success">
                                            <i class="bi bi-check-circle"></i> Disponible
                                        </span>
                                    </c:if>
                                </div>
                                
                                <p class="card-text text-truncate" style="max-height: 3em;">
                                    ${salle.description}
                                </p>
                                
                                <div class="d-flex justify-content-between align-items-center mt-3">
                                    <h5 class="text-primary mb-0">
                                        <fmt:formatNumber value="${salle.prixParJour}" type="number" /> FCFA
                                        <small class="text-muted">/jour</small>
                                    </h5>
                                </div>
                            </div>
                            
                            <div class="card-footer bg-white">
                                <div class="d-grid gap-2">
                                    <a href="${pageContext.request.contextPath}/user/salles/${salle.id}" 
                                    class="btn btn-outline-primary">
                                        <i class="bi bi-eye"></i> Voir détails
                                    </a>
                                    <c:if test="${salle.disponible}">
                                        <a href="${pageContext.request.contextPath}/user/reservations/new?salleId=${salle.id}" 
                                        class="btn btn-primary">
                                            <i class="bi bi-calendar-check"></i> Réserver
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Pagination -->
<c:set var="pageObj" value="${sallesPage}" scope="request" />
<jsp:include page="../common/pagination.jsp">
    <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/user/salles" />
    <jsp:param name="queryParams" value="${not empty localisation ? '&localisation='.concat(localisation) : ''}${not empty capaciteMin ? '&capaciteMin='.concat(capaciteMin) : ''}${not empty prixMax ? '&prixMax='.concat(prixMax) : ''}" />
</jsp:include>

<style>
    .hover-shadow {
        transition: box-shadow 0.3s ease;
    }
    .hover-shadow:hover {
        box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
    }
</style>

<jsp:include page="../common/footer.jsp" />