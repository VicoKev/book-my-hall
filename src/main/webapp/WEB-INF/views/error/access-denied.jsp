<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Accès Refusé" scope="request"/>
<jsp:include page="../common/header.jsp" />

<div class="text-center py-5">
    <i class="bi bi-shield-exclamation text-danger" style="font-size: 6rem;"></i>
    <h1 class="mt-4">Accès Refusé</h1>
    <p class="lead text-muted">Vous n'avez pas les autorisations nécessaires pour accéder à cette page.</p>
    <div class="mt-4">
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
            <i class="bi bi-house"></i> Retour à l'accueil
        </a>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />