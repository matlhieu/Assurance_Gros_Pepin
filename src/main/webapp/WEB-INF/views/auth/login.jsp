<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Connexion – Assurance Gros Pépin</title>
    <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png">
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=5">
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="login">
        <!-- Partie gauche : visuel -->
        <div class="login-visuel" aria-hidden="true">
            <span class="bulle bulle-1"></span>
            <span class="bulle bulle-2"></span>
            <span class="bulle bulle-3"></span>
            <img src="${pageContext.request.contextPath}/image/logo.png" alt="" class="login-visuel-logo">
            <p>Vos contrats, vos sinistres et vos paiements, au même endroit.</p>
        </div>

        <!-- Partie droite : formulaire -->
        <div class="login-formulaire">
            <img src="${pageContext.request.contextPath}/image/logo.png" alt="Gros Pépin" class="login-logo">

            <h1 class="login-titre">Connexion</h1>

            <%-- Message d'erreur envoyé par LoginServlet : req.setAttribute("erreur", ...) --%>
            <% if (request.getAttribute("erreur") != null) { %>
                <p class="erreur">${erreur}</p>
            <% } %>

            <%-- Formulaire envoyé en POST à LoginServlet (/login) --%>
            <form method="post" action="${pageContext.request.contextPath}/login">
                <label for="email">Adresse e-mail</label>
                <input type="email" id="email" name="email" value="${param.email}" required>

                <label for="motDePasse">Mot de passe</label>
                <input type="password" id="motDePasse" name="motDePasse" required>

                <a href="#" class="lien-petit">Mot de passe oublié ?</a>

                <input type="submit" value="Se connecter" class="bouton-valider">
            </form>

            <a href="${pageContext.request.contextPath}/register" class="lien-fleche">
                <svg viewBox="0 0 24 24"><path d="M9 5l7 7-7 7"/></svg>
                Pas encore client ? Créez votre espace personnel
            </a>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
