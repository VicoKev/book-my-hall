<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${salleDTO.id != null ? 'Modifier' : 'Ajouter'} une Salle" scope="request"/>
<jsp:include page="../common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header bg-white">
                <h4 class="mb-0">
                    <i class="bi bi-building-fill"></i> ${salleDTO.id != null ? 'Modifier' : 'Ajouter'} une Salle
                </h4>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/salles/${salleDTO.id != null ? 'edit/' += salleDTO.id : 'add'}"
                      method="post" enctype="multipart/form-data">
                    
                    <div class="mb-3">
                        <label for="nom" class="form-label">Nom de la salle *</label>
                        <input type="text" class="form-control" id="nom" name="nom" 
                               value="${salleDTO.nom}" required>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="capacite" class="form-label">Capacité *</label>
                            <input type="number" class="form-control" id="capacite" name="capacite" 
                                   value="${salleDTO.capacite}" min="10" max="1000" required>
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="prixParJour" class="form-label">Prix par jour (FCFA) *</label>
                            <input type="number" class="form-control" id="prixParJour" name="prixParJour" 
                                   value="${salleDTO.prixParJour}" min="1" step="0.01" required>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="localisation" class="form-label">Localisation *</label>
                        <input type="text" class="form-control" id="localisation" name="localisation" 
                               value="${salleDTO.localisation}" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="description" class="form-label">Description</label>
                        <textarea class="form-control" id="description" name="description" rows="3">${salleDTO.description}</textarea>
                    </div>
                    
                    <div class="mb-3">
                        <label for="equipements" class="form-label">Équipements</label>
                        <textarea class="form-control" id="equipements" name="equipements" rows="2" 
                                  placeholder="Ex: Climatisation, Sonorisation, Tables et chaises">${salleDTO.equipements}</textarea>
                    </div>
                    
                    <div class="mb-3">
                        <label for="imageFile" class="form-label">Image de la salle</label>
                        <input type="file" class="form-control" id="imageFile" name="imageFile"
                               accept="image/jpeg,image/png,image/gif">
                        <c:if test="${not empty salleDTO.imageFileName}">
                            <small class="form-text text-muted">Fichier actuel: ${salleDTO.imageFileName}</small>
                        </c:if>
                    </div>
                    
                    <div class="form-check mb-3">
                        <input type="checkbox" class="form-check-input" id="disponible" name="disponible" 
                               ${salleDTO.disponible || salleDTO.id == null ? 'checked' : ''}>
                        <label class="form-check-label" for="disponible">
                            Salle disponible
                        </label>
                    </div>
                    
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-save"></i> Enregistrer
                        </button>
                        <a href="${pageContext.request.contextPath}/salles" class="btn btn-secondary">
                            <i class="bi bi-x-circle"></i> Annuler
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />