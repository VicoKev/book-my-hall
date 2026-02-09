<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- 
    Paramètres attendus :
    - pageObj : l'objet Page (sallesPage, reservationsPage, usersPage, etc.)
    - baseUrl : l'URL de base pour les liens de pagination
    - queryParams : (optionnel) paramètres de requête supplémentaires (ex: &statut=PENDING)
--%>

<c:if test="${not empty pageObj and pageObj.totalPages > 1}">
    <nav aria-label="Pagination">
        <ul class="pagination justify-content-center mt-4">
            <!-- Premier -->
            <li class="page-item ${pageObj.first ? 'disabled' : ''}">
                <a class="page-link" href="${baseUrl}?page=0${queryParams}" aria-label="Premier">
                    <span aria-hidden="true">&laquo;&laquo;</span>
                </a>
            </li>
            
            <!-- Précédent -->
            <li class="page-item ${pageObj.first ? 'disabled' : ''}">
                <a class="page-link" href="${baseUrl}?page=${pageObj.number - 1}${queryParams}" aria-label="Précédent">
                    <span aria-hidden="true">&laquo;</span>
                </a>
            </li>

            <!-- Pages -->
            <c:forEach begin="0" end="${pageObj.totalPages - 1}" var="i">
                <c:if test="${i >= pageObj.number - 2 and i <= pageObj.number + 2}">
                    <li class="page-item ${pageObj.number == i ? 'active' : ''}">
                        <a class="page-link" href="${baseUrl}?page=${i}${queryParams}">${i + 1}</a>
                    </li>
                </c:if>
            </c:forEach>

            <!-- Suivant -->
            <li class="page-item ${pageObj.last ? 'disabled' : ''}">
                <a class="page-link" href="${baseUrl}?page=${pageObj.number + 1}${queryParams}" aria-label="Suivant">
                    <span aria-hidden="true">&raquo;</span>
                </a>
            </li>
            
            <!-- Dernier -->
            <li class="page-item ${pageObj.last ? 'disabled' : ''}">
                <a class="page-link" href="${baseUrl}?page=${pageObj.totalPages - 1}${queryParams}" aria-label="Dernier">
                    <span aria-hidden="true">&raquo;&raquo;</span>
                </a>
            </li>
        </ul>
    </nav>
</c:if>
