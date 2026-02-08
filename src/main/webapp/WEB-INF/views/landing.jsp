<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Accueil" scope="request"/>
<jsp:include page="common/header.jsp" />

<!-- Hero Section -->
<div class="py-5 text-center bg-light">
    <div class="container">
        <h1 class="display-3 fw-bold text-primary mb-3">
            <i class="bi bi-building"></i> BookMyHall
        </h1>
        <p class="lead mb-4">
            Réservez votre salle de fête idéale en quelques clics
        </p>
        <p class="text-muted mb-4">
            Plus de 5 salles disponibles pour tous vos événements : mariages, anniversaires, 
            séminaires, conférences et bien plus encore !
        </p>
        <div class="d-grid gap-2 d-md-flex justify-content-md-center">
            <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-lg px-4">
                <i class="bi bi-person-plus"></i> S'inscrire Gratuitement
            </a>
            <a href="${pageContext.request.contextPath}/salles" class="btn btn-outline-primary btn-lg px-4">
                <i class="bi bi-search"></i> Découvrir les Salles
            </a>
        </div>
    </div>
</div>

<!-- Features Section -->
<div class="container my-5">
    <div class="row g-4">
        <div class="col-md-4">
            <div class="card h-100 text-center p-4">
                <div class="card-body">
                    <i class="bi bi-search text-primary" style="font-size: 3rem;"></i>
                    <h3 class="card-title mt-3">Recherchez</h3>
                    <p class="card-text">
                        Trouvez la salle parfaite parmi notre large sélection. 
                        Filtrez par capacité, localisation et budget.
                    </p>
                </div>
            </div>
        </div>
        
        <div class="col-md-4">
            <div class="card h-100 text-center p-4">
                <div class="card-body">
                    <i class="bi bi-calendar-check text-success" style="font-size: 3rem;"></i>
                    <h3 class="card-title mt-3">Réservez</h3>
                    <p class="card-text">
                        Réservez en ligne facilement et rapidement. 
                        Système de vérification des disponibilités en temps réel.
                    </p>
                </div>
            </div>
        </div>
        
        <div class="col-md-4">
            <div class="card h-100 text-center p-4">
                <div class="card-body">
                    <i class="bi bi-emoji-smile text-warning" style="font-size: 3rem;"></i>
                    <h3 class="card-title mt-3">Célébrez</h3>
                    <p class="card-text">
                        Profitez de votre événement dans la salle de vos rêves. 
                        Équipements modernes et service de qualité.
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Call to Action -->
<div class="bg-primary text-white text-center py-5 my-5">
    <div class="container">
        <h2 class="mb-3">Prêt à réserver votre salle ?</h2>
        <p class="lead mb-4">Rejoignez des centaines de clients satisfaits</p>
        <a href="${pageContext.request.contextPath}/register" class="btn btn-light btn-lg">
            Commencer Maintenant
        </a>
    </div>
</div>

<jsp:include page="common/footer.jsp" />