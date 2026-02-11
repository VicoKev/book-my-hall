<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Contactez-nous" scope="request" />
<jsp:include page="common/header.jsp" />

<div class="row justify-content-center">
  <div class="col-lg-10">
    <div class="card shadow-sm border-0 overflow-hidden">
      <div class="row g-0">
        <!-- Informational Sidebar -->
        <div class="col-md-4 bg-primary text-white p-4 p-lg-5">
          <h2 class="h4 mb-4">Informations de contact</h2>
          <p class="mb-4">
            Nous sommes là pour vous aider. N'hésitez pas à nous contacter pour
            toute question ou demande spéciale.
          </p>

          <ul class="list-unstyled mb-0">
            <li class="d-flex mb-3">
              <i class="bi bi-geo-alt-fill me-3 fs-5"></i>
              <div>
                <strong>Adresse</strong><br />
                Campus d'Abomey-Calavi, Bénin
              </div>
            </li>
            <li class="d-flex mb-3">
              <i class="bi bi-envelope-fill me-3 fs-5"></i>
              <div>
                <strong>Email</strong><br />
                contact@bookmyhall.bj
              </div>
            </li>
            <li class="d-flex mb-3">
              <i class="bi bi-telephone-fill me-3 fs-5"></i>
              <div>
                <strong>Téléphone</strong><br />
                +229 01 000 000
              </div>
            </li>
            <li class="d-flex">
              <i class="bi bi-clock-fill me-3 fs-5"></i>
              <div>
                <strong>Horaires</strong><br />
                Lun - Ven: 8h - 18h
              </div>
            </li>
          </ul>

          <div class="mt-5">
            <h3 class="h6 mb-3">Suivez-nous</h3>
            <div class="d-flex gap-3">
              <a href="#" class="text-white fs-4"
                ><i class="bi bi-facebook"></i
              ></a>
              <a href="#" class="text-white fs-4"
                ><i class="bi bi-twitter"></i
              ></a>
              <a href="#" class="text-white fs-4"
                ><i class="bi bi-linkedin"></i
              ></a>
              <a href="#" class="text-white fs-4"
                ><i class="bi bi-instagram"></i
              ></a>
            </div>
          </div>
        </div>

        <!-- Contact Form -->
        <div class="col-md-8 p-4 p-lg-5 bg-white">
          <h1 class="h3 mb-4">Envoyez-nous un message</h1>
          <p class="text-muted mb-4">
            Utilisez le formulaire ci-dessous pour nous envoyer votre message.
            Nous vous répondrons dans les plus brefs délais.
          </p>

          <form
            onsubmit="
              alert('Merci ! Votre message a été simulé avec succès.');
              return false;
            "
          >
            <div class="row g-3">
              <div class="col-md-6">
                <label for="nom" class="form-label">Nom complet</label>
                <input
                  type="text"
                  class="form-control"
                  id="nom"
                  placeholder="Votre nom"
                  required
                />
              </div>
              <div class="col-md-6">
                <label for="email" class="form-label">Adresse Email</label>
                <input
                  type="email"
                  class="form-control"
                  id="email"
                  placeholder="nom@exemple.com"
                  required
                />
              </div>
              <div class="col-12">
                <label for="sujet" class="form-label">Sujet</label>
                <input
                  type="text"
                  class="form-control"
                  id="sujet"
                  placeholder="Quel est l'objet de votre message ?"
                  required
                />
              </div>
              <div class="col-12">
                <label for="message" class="form-label">Message</label>
                <textarea
                  class="form-control"
                  id="message"
                  rows="5"
                  placeholder="Détaillez votre demande ici..."
                  required
                ></textarea>
              </div>
              <div class="col-12 mt-4">
                <button type="submit" class="btn btn-primary btn-lg px-4">
                  <i class="bi bi-send me-2"></i> Envoyer le message
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<jsp:include page="common/footer.jsp" />
