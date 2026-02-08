<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="pageTitle" value="Inscription" scope="request"/>
<jsp:include page="common/header.jsp" />

<style>
    .required-asterisk {
        color: red;
    }
</style>

<div class="row justify-content-center mb-4">
    <div class="col-md-6">
        <div class="card shadow-sm">
            <div class="card-body p-5">
                <h2 class="text-center mb-4">
                    <i class="bi bi-person-plus text-primary"></i> Inscription
                </h2>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle"></i> ${successMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/register" method="post">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="prenom" class="form-label">Prénom <span class="required-asterisk">*</span></label>
                            <input type="text" class="form-control" id="prenom" 
                                   name="prenom" required placeholder="Votre prénom">
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="nom" class="form-label">Nom <span class="required-asterisk">*</span></label>
                            <input type="text" class="form-control" id="nom" 
                                   name="nom" required placeholder="Votre nom">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="username" class="form-label">Nom d'utilisateur <span class="required-asterisk">*</span></label>
                        <input type="text" class="form-control" id="username" 
                               name="username" required minlength="3" 
                               placeholder="Choisissez un nom d'utilisateur">
                        <small class="form-text text-muted">Minimum 3 caractères</small>
                    </div>
                    
                    <div class="mb-3">
                        <label for="email" class="form-label">Email <span class="required-asterisk">*</span></label>
                        <input type="email" class="form-control" id="email" 
                               name="email" required placeholder="exemple@bookmyhall.com">
                    </div>
                    
                    <div class="mb-3">
                        <label for="telephone" class="form-label">Téléphone <span class="required-asterisk">*</span></label>
                        <input type="tel" class="form-control" id="telephone" 
                               name="telephone" pattern="^\+[0-9]{10,15}$" 
                               placeholder="+22901********" required>
                        <small class="form-text text-muted">Ex: +2290000000000</small>
                    </div>
                    
                    <div class="mb-3">
                        <label for="password" class="form-label">Mot de passe <span class="required-asterisk">*</span></label>
                        <input type="password" class="form-control" id="password" 
                               name="password" required minlength="6" 
                               placeholder="Mot de passe">
                        <small class="form-text text-muted">6 caractères minimum</small>
                    </div>
                    
                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Confirmer le mot de passe <span class="required-asterisk">*</span></label>
                        <input type="password" class="form-control" id="confirmPassword" 
                               name="confirmPassword" required 
                               placeholder="Retapez votre mot de passe">
                    </div>
                    
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            <i class="bi bi-check-circle"></i> S'inscrire
                        </button>
                    </div>
                </form>
                
                <hr class="my-4">
                
                <p class="text-center mb-0">
                    Déjà un compte ? 
                    <a href="${pageContext.request.contextPath}/login" class="text-decoration-none fw-bold">
                        Se connecter
                    </a>
                </p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp" />