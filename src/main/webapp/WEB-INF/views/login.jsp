<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Connexion" scope="request"/>
<jsp:include page="common/header.jsp" />

<div class="row justify-content-center mt-5">
    <div class="col-md-5">
        <div class="card shadow-sm">
            <div class="card-body p-5">
                <h2 class="text-center mb-4">
                    <i class="bi bi-box-arrow-in-right text-primary"></i> Connexion
                </h2>
                
                <form action="${pageContext.request.contextPath}/login" method="post">
                    <div class="mb-3">
                        <label for="username" class="form-label">Nom d'utilisateur</label>
                        <input type="text" class="form-control" id="username" 
                               name="username" required autofocus 
                               placeholder="Votre nom d'utilisateur">
                    </div>
                    
                    <div class="mb-3">
                        <label for="password" class="form-label">Mot de passe</label>
                        <input type="password" class="form-control" id="password" 
                               name="password" required 
                               placeholder="Votre mot de passe">
                    </div>
                    
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            <i class="bi bi-check-circle"></i> Se connecter
                        </button>
                    </div>
                </form>
                
                <hr class="my-4">
                
                <p class="text-center mb-0">
                    Pas encore de compte ? 
                    <a href="${pageContext.request.contextPath}/register" class="text-decoration-none fw-bold">
                        S'inscrire maintenant
                    </a>
                </p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp" />