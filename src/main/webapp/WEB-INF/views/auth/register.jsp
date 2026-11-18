<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion — Assurance</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="auth-container">
    <div class="auth-card">
        <h2>Connexion</h2>

        <c:if test="${not empty sessionScope.flashSuccess}">
            <div class="alert alert-success">${sessionScope.flashSuccess}</div>
            <c:remove var="flashSuccess" scope="session" />
        </c:if>

     
        <c:if test="${not empty erreur}">
            <div class="alert alert-danger">${erreur}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login" novalidate>
            <div class="form-group">
                <label for="email">Email *</label>
                <input type="email"
                       id="email"
                       name="email"
                       value="${not empty email ? email : ''}"
                       placeholder="votre.email@exemple.fr"
                       autocomplete="email"
                       required />
            </div>

            <div class="form-group">
                <label for="motDePasse">Mot de passe *</label>
                <input type="password"
                       id="motDePasse"
                       name="motDePasse"
                       placeholder="Votre mot de passe"
                       autocomplete="current-password"
                       required />
            </div>

            <button type="submit" class="btn btn-primary btn-block">
                Se connecter
            </button>
        </form>

        <p class="auth-footer">
            Pas encore de compte ?
            <a href="${pageContext.request.contextPath}/register">Créer un compte</a>
        </p>
    </div>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
</body>
</html>