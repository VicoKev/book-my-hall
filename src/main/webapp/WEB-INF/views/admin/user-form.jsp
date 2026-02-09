<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${utilisateurDTO.id != null ? 'Modifier' : 'Ajouter'} Utilisateur" scope="request"/>
<jsp:include page="../common/header.jsp" />

<style>
    .required-asterisk {
        color: red;
    }
</style>

<div class="row justify-content-center mb-4">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header bg-white">
                <h4 class="mb-0">
                    <i class="bi bi-person${utilisateurDTO.id != null ? '-check' : '-plus'}"></i>
                    ${utilisateurDTO.id != null ? 'Modifier' : 'Ajouter'} un Utilisateur
                </h4>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${utilisateurDTO.id != null}">
                        <c:set var="actionUrl" value="${pageContext.request.contextPath}/admin/users/${utilisateurDTO.id}/update" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="actionUrl" value="${pageContext.request.contextPath}/admin/users/add" />
                    </c:otherwise>
                </c:choose>
                <form action="${actionUrl}" method="post" modelAttribute="utilisateurDTO">
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="prenom" class="form-label">Prénom <span class="required-asterisk">*</span></label>
                            <input type="text" class="form-control" id="prenom" name="prenom" 
                                   value="${utilisateurDTO.prenom}" required>
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="nom" class="form-label">Nom <span class="required-asterisk">*</span></label>
                            <input type="text" class="form-control" id="nom" name="nom" 
                                   value="${utilisateurDTO.nom}" required>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="email" class="form-label">Email <span class="required-asterisk">*</span></label>
                        <input type="email" class="form-control" id="email" name="email" 
                               value="${utilisateurDTO.email}" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="telephone" class="form-label">Téléphone <span class="required-asterisk">*</span></label>
                        <input type="tel" class="form-control" id="telephone" name="telephone" 
                               value="${utilisateurDTO.telephone}" pattern="^\+[0-9]{10,15}$" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="username" class="form-label">Nom d'utilisateur <span class="required-asterisk">*</span></label>
                        <input type="text" class="form-control" id="username" name="username" 
                               value="${utilisateurDTO.username}" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="password" class="form-label">
                            Mot de passe ${utilisateurDTO.id != null ? '(laisser vide pour ne pas changer)' : '*'}
                        </label>
                        <input type="password" class="form-control" id="password" name="password"
                               ${utilisateurDTO.id == null ? 'required' : ''} minlength="6">
                    </div>

                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Confirmer le mot de passe ${utilisateurDTO.id == null ? '*' : ''}</label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                               ${utilisateurDTO.id == null ? 'required' : ''} minlength="6">
                        <div class="form-text">${utilisateurDTO.id != null ? 'Laissez vide pour conserver le mot de passe actuel' : ''}</div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="role" class="form-label">Rôle <span class="required-asterisk">*</span></label>
                        <select class="form-select" id="role" name="role" required>
                            <c:forEach items="${roles}" var="roleOption">
                                <option value="${roleOption}" ${utilisateurDTO.role == roleOption ? 'selected' : ''}>
                                    ${roleOption}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-check mb-3">
                        <input type="hidden" name="actif" value="false">
                        <input type="checkbox" class="form-check-input" id="actif" name="actif" value="true"
                               ${utilisateurDTO.actif ? 'checked' : ''}>
                        <label class="form-check-label" for="actif">
                            Compte actif
                        </label>
                    </div>
                    
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-save"></i> Enregistrer
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">
                            <i class="bi bi-x-circle"></i> Annuler
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />