<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - BookMyHall</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    
    <style>
        :root {
            --primary-color: #6366f1;
            --secondary-color: #8b5cf6;
        }

        html, body {
            height: 100%;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8fafc;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }

        .main-content {
            flex: 1 0 auto;
        }

        .navbar-brand {
            font-weight: 700;
            color: var(--primary-color) !important;
            font-size: 1.5rem;
        }

        .navbar {
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .btn-primary {
            background-color: var(--primary-color);
            border-color: var(--primary-color);
        }

        .btn-primary:hover {
            background-color: var(--secondary-color);
            border-color: var(--secondary-color);
        }

        .card {
            border: none;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            border-radius: 0.5rem;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-light bg-white mb-4">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <i class="bi bi-building-fill"></i> BookMyHall
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <sec:authorize access="!isAuthenticated()">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/">Accueil</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/login">Connexion</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link btn btn-primary text-white ms-2 px-3" 
                            href="${pageContext.request.contextPath}/register">
                                Inscription
                            </a>
                        </li>
                    </sec:authorize>
                    
                    <sec:authorize access="isAuthenticated()">
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <li class="nav-item">
                                <a class="nav-link text-danger fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">
                                    <i class="bi bi-shield-check"></i> Dashboard
                                </a>
                            </li>
                        </sec:authorize>
                        <sec:authorize access="hasAuthority('USER') and !hasAuthority('ADMIN')">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/user/dashboard">
                                    <i class="bi bi-speedometer2"></i> Dashboard
                                </a>
                            </li>
                        </sec:authorize>
                        <li class="nav-item">
                            <span class="nav-link">
                                <i class="bi bi-person-circle"></i> 
                                <sec:authentication property="principal.username" />
                            </span>
                        </li>
                        <li class="nav-item">
                            <form action="${pageContext.request.contextPath}/logout" method="post" style="display:inline;">
                                <button type="submit" class="btn btn-link nav-link text-danger">
                                    <i class="bi bi-box-arrow-right"></i> Déconnexion
                                </button>
                            </form>
                        </li>
                    </sec:authorize>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Messages Flash -->
    <div class="container">
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle"></i> ${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <div class="container">