<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="${isEdit ? 'Éditer Salle' : 'Nouvelle Salle'}" scope="request"/>
<jsp:include page="../common/header.jsp" />

<!-- Breadcrumb -->
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a>
        </li>
        <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/admin/salles">Salles</a>
        </li>
        <li class="breadcrumb-item active">
            ${isEdit ? 'Éditer' : 'Nouvelle'}
        </li>
    </ol>
</nav>

<div class="row justify-content-center mb-4">
    <div class="col-lg-8">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">
                    <i class="bi bi-building-fill"></i> 
                    ${isEdit ? 'Éditer la Salle' : 'Nouvelle Salle'}
                </h5>
            </div>
            <div class="card-body">
                <c:set var="formAction" value="${isEdit ? '/admin/salles/'.concat(salleDTO.id).concat('/update') : '/admin/salles/create'}" />
                
                <form action="${pageContext.request.contextPath}${formAction}" method="post" enctype="multipart/form-data">
                    <!-- Nom -->
                    <div class="mb-3">
                        <label for="nom" class="form-label">Nom de la salle *</label>
                        <input type="text" class="form-control" id="nom" name="nom" 
                               value="${salleDTO.nom}" required minlength="3" maxlength="100">
                    </div>
                    
                    <!-- Localisation -->
                    <div class="mb-3">
                        <label for="localisation" class="form-label">Localisation *</label>
                        <input type="text" class="form-control" id="localisation" name="localisation" 
                               value="${salleDTO.localisation}" required minlength="5" maxlength="200">
                    </div>
                    
                    <!-- Capacité et Prix -->
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="capacite" class="form-label">Capacité (personnes) *</label>
                            <input type="number" class="form-control" id="capacite" name="capacite" 
                                   value="${salleDTO.capacite}" required min="10" max="1000">
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="prixParJour" class="form-label">Prix par jour (FCFA) *</label>
                            <input type="number" class="form-control" id="prixParJour" name="prixParJour" 
                                   value="${salleDTO.prixParJour}" required >
                        </div>
                    </div>
                    
                    <!-- Description -->
                    <div class="mb-3">
                        <label for="description" class="form-label">Description</label>
                        <textarea class="form-control" id="description" name="description" 
                                  rows="4" maxlength="500">${salleDTO.description}</textarea>
                        <small class="form-text text-muted">Maximum 500 caractères</small>
                    </div>
                    
                    <!-- Équipements -->
                    <div class="mb-3">
                        <label for="equipements" class="form-label">Équipements</label>
                        <textarea class="form-control" id="equipements" name="equipements" 
                                  rows="3" maxlength="500" 
                                  placeholder="Ex: Climatisation, Sonorisation, Projecteur...">${salleDTO.equipements}</textarea>
                        <small class="form-text text-muted">Séparez par des virgules</small>
                    </div>
                    
                    <!-- Image Upload -->
                    <div class="mb-3">
                        <label for="imageFile" class="form-label">Image de la salle</label>
                        <input type="file" class="form-control" id="imageFile" name="imageFile"
                               accept="image/jpeg,image/png,image/gif">
                        <c:if test="${not empty salleDTO.imageFileName}">
                            <small class="form-text text-muted">Fichier actuel: ${salleDTO.imageFileName}</small>
                        </c:if>
                    </div>
                    
                    <!-- Disponible -->
                    <div class="mb-3">
                        <input type="hidden" name="_disponible" value="on">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="disponible"
                                   name="disponible" value="true"
                                   ${salleDTO.disponible ? 'checked' : ''}>
                            <label class="form-check-label" for="disponible">
                                Salle disponible pour réservation
                            </label>
                        </div>
                    </div>
                    
                    <!-- Boutons -->
                    <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                        <a href="${pageContext.request.contextPath}/admin/salles" 
                           class="btn btn-outline-secondary">
                            <i class="bi bi-x-circle"></i> Annuler
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-check-circle"></i> 
                            ${isEdit ? 'Mettre à jour' : 'Créer'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />