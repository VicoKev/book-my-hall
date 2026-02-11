<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Mon Profil" scope="request"/>
<jsp:include page="../common/header.jsp" />

<div class="row">
    <div class="col-md-4">
        <!-- Profile Card -->
        <div class="card">
            <div class="card-body text-center">
                <div class="mb-3">
                    <i class="bi bi-person-circle text-primary" style="font-size: 6rem;"></i>
                </div>
                <h4 class="mb-0">${user.prenom} ${user.nom}</h4>
                <p class="text-muted">@${user.username}</p>
                
                <c:choose>
                    <c:when test="${user.role == 'ADMIN'}">
                        <span class="badge bg-danger">
                            <i class="bi bi-shield-check"></i> Administrateur
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-primary">
                            <i class="bi bi-person"></i> Utilisateur
                        </span>
                    </c:otherwise>
                </c:choose>
                
                <hr>
                
                <div class="d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-outline-primary">
                        <i class="bi bi-speedometer2"></i> Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/user/reservations" class="btn btn-outline-success">
                        <i class="bi bi-calendar-check"></i> Mes Réservations
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <div class="col-md-8">
        <!-- Profile Information -->
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0"><i class="bi bi-person-lines-fill"></i> Informations Personnelles</h5>
            </div>
            <div class="card-body">
                <table class="table table-borderless">
                    <tbody>
                        <tr>
                            <th width="30%"><i class="bi bi-person"></i> Prénom :</th>
                            <td>${user.prenom}</td>
                        </tr>
                        <tr>
                            <th><i class="bi bi-person"></i> Nom :</th>
                            <td>${user.nom}</td>
                        </tr>
                        <tr>
                            <th><i class="bi bi-person-badge"></i> Nom d'utilisateur :</th>
                            <td>${user.username}</td>
                        </tr>
                        <tr>
                            <th><i class="bi bi-envelope"></i> Email :</th>
                            <td>${user.email}</td>
                        </tr>
                        <tr>
                            <th><i class="bi bi-telephone"></i> Téléphone :</th>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty user.telephone}">
                                        ${user.telephone}
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">Non renseigné</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </tbody>
                </table>
                
                <hr>
                
                <div class="alert alert-info" role="alert">
                    <i class="bi bi-info-circle"></i> 
                    <strong>Note :</strong> Pour modifier vos informations, veuillez contacter un administrateur.
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />