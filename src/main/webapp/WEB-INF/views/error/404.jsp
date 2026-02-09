<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Page Non Trouvée" scope="request" />
<jsp:include page="../common/header.jsp" />

<div class="container py-5 mt-5">
  <div class="row justify-content-center">
    <div class="col-md-6 text-center">
      <div class="mb-4">
        <i
          class="bi bi-exclamation-octagon text-warning"
          style="font-size: 8rem"
        ></i>
      </div>
      <h1 class="display-1 fw-bold">404</h1>
      <h2 class="mb-4">Oups ! Page introuvable</h2>
      <p class="lead text-muted mb-5">
        Désolé, la page que vous recherchez n'existe pas ou a été déplacée.
      </p>
      <div class="d-grid gap-2 d-sm-flex justify-content-sm-center">
        <button
          onclick="history.back()"
          class="btn btn-outline-primary btn-lg px-4 gap-3"
        >
          <i class="bi bi-arrow-left"></i> Retourner à la page précédente
        </button>
      </div>
    </div>
  </div>
</div>

<jsp:include page="../common/footer.jsp" />
