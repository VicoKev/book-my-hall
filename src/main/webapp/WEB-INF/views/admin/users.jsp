<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Gestion des Utilisateurs" scope="request"/>
<jsp:include page="../common/header.jsp" />

<!-- Page Header -->
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2><i class="bi bi-people"></i> Gestion des Utilisateurs</h2>
        <p class="text-muted mb-0">
            Total: ${usersPage.totalElements} utilisateurs
            (${totalAdmins} admin(s), ${totalUsers} utilisateur(s))
        </p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/admin/users/add" class="btn btn-primary">
            <i class="bi bi-person-plus"></i> Ajouter un utilisateur
        </a>
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary ms-2">
            <i class="bi bi-arrow-left"></i> Retour
        </a>
    </div>
</div>

<!-- Users Table -->
<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Nom Complet</th>
                        <th>Email</th>
                        <th>Username</th>
                        <th>Rôle</th>
                        <th>Statut</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${users}" var="user">
                        <tr>
                            <td>#${user.id}</td>
                            <td>${user.nomComplet}</td>
                            <td>${user.email}</td>
                            <td><code>${user.username}</code></td>
                            <td>
                                <c:choose>
                                    <c:when test="${user.role == 'ADMIN'}">
                                        <span class="badge bg-danger">ADMIN</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-primary">USER</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${user.actif}">
                                        <span class="badge bg-success">Actif</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">Inactif</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="btn-group btn-group-sm gap-1">
                                    <!-- Change Role -->
                                    <c:choose>
                                        <c:when test="${user.role == 'USER'}">
                                            <form action="${pageContext.request.contextPath}/admin/users/${user.id}/change-role" 
                                                  method="post" style="display:inline;">
                                                <input type="hidden" name="newRole" value="ADMIN">
                                                <button type="submit" class="btn btn-outline-danger" 
                                                        title="Promouvoir en Admin"
                                                        onclick="return confirm('Promouvoir en ADMIN ?');">
                                                    <i class="bi bi-shield-check"></i>
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="${pageContext.request.contextPath}/admin/users/${user.id}/change-role" 
                                                  method="post" style="display:inline;">
                                                <input type="hidden" name="newRole" value="USER">
                                                <button type="submit" class="btn btn-outline-primary" 
                                                        title="Rétrograder en User"
                                                        onclick="return confirm('Rétrograder en USER ?');">
                                                    <i class="bi bi-person"></i>
                                                </button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                    
                                    <!-- Toggle Active -->
                                    <form action="${pageContext.request.contextPath}/admin/users/${user.id}/toggle-active" 
                                          method="post" style="display:inline;">
                                        <c:choose>
                                            <c:when test="${user.actif}">
                                                <input type="hidden" name="actif" value="false">
                                                <button type="submit" class="btn btn-outline-warning" 
                                                        title="Désactiver">
                                                    <i class="bi bi-x-circle"></i>
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="hidden" name="actif" value="true">
                                                <button type="submit" class="btn btn-outline-success" 
                                                        title="Activer">
                                                    <i class="bi bi-check-circle"></i>
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                    
                                    <!-- Edit -->
                                    <a href="${pageContext.request.contextPath}/admin/users/${user.id}/edit"
                                       class="btn btn-outline-info" title="Modifier">
                                        <i class="bi bi-pencil"></i>
                                    </a>

                                    <!-- Delete (danger!) -->
                                    <form action="${pageContext.request.contextPath}/admin/users/${user.id}/delete"
                                          method="post" style="display:inline;"
                                          onsubmit="return confirm('ATTENTION : Supprimer cet utilisateur ?');">
                                        <button type="submit" class="btn btn-outline-danger" title="Supprimer">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty users}">
                        <tr>
                            <td colspan="7" class="text-center py-4">
                                <div class="text-muted">
                                    <i class="bi bi-person-x fs-1 d-block mb-2"></i>
                                    Aucun utilisateur trouvé
                                </div>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
        
        <!-- Pagination -->
        <c:set var="pageObj" value="${usersPage}" scope="request" />
        <jsp:include page="../common/pagination.jsp">
            <jsp:param name="baseUrl" value="${pageContext.request.contextPath}/admin/users" />
        </jsp:include>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />