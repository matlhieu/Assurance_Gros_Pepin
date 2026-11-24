<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%!
    private String escapeHtml(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
    Map<?, ?> erreurs = (Map<?, ?>) request.getAttribute("erreurs");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inscription – Assurance Gros Pépin</title>
    <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png">
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=6">
    <style>
        .login-formulaire input[type="text"],
        .login-formulaire input[type="tel"] {
            height: 56px;
            margin-bottom: 22px;
            padding: 0 14px;
            font: inherit;
            font-size: 1.05rem;
            border: 1px solid #8C8898;
            border-radius: 4px;
        }

        .login-formulaire input[type="text"]:focus,
        .login-formulaire input[type="tel"]:focus {
            outline: none;
            border-color: var(--texte);
            box-shadow: 0 0 0 2px #fff, 0 0 0 4px var(--texte);
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="login">
        <div class="login-visuel" aria-hidden="true">
            <span class="bulle bulle-1"></span>
            <span class="bulle bulle-2"></span>
            <span class="bulle bulle-3"></span>
            <img src="${pageContext.request.contextPath}/image/logo.png" alt="" class="login-visuel-logo">
            <p>Vos contrats, vos sinistres et vos paiements, au même endroit.</p>
        </div>

        <div class="login-formulaire">
            <img src="${pageContext.request.contextPath}/image/logo.png" alt="Gros Pépin" class="login-logo">
            <h1 class="login-titre">Créer votre compte</h1>

            <form method="post" action="${pageContext.request.contextPath}/register">
                <label for="prenom">Prénom</label>
                <input type="text" id="prenom" name="prenom" value="<%= escapeHtml(request.getAttribute("prenom")) %>"
                       maxlength="50" autocomplete="given-name" required>
                <% if (erreurs != null && erreurs.get("prenom") != null) { %>
                    <p class="erreur"><%= escapeHtml(erreurs.get("prenom")) %></p>
                <% } %>

                <label for="nom">Nom</label>
                <input type="text" id="nom" name="nom" value="<%= escapeHtml(request.getAttribute("nom")) %>"
                       maxlength="50" autocomplete="family-name" required>
                <% if (erreurs != null && erreurs.get("nom") != null) { %>
                    <p class="erreur"><%= escapeHtml(erreurs.get("nom")) %></p>
                <% } %>

                <label for="email">Adresse e-mail</label>
                <input type="email" id="email" name="email" value="<%= escapeHtml(request.getAttribute("email")) %>"
                       maxlength="100" autocomplete="email" required>
                <% if (erreurs != null && erreurs.get("email") != null) { %>
                    <p class="erreur"><%= escapeHtml(erreurs.get("email")) %></p>
                <% } %>

                <label for="telephone">Téléphone</label>
                <input type="tel" id="telephone" name="telephone" value="<%= escapeHtml(request.getAttribute("telephone")) %>"
                       maxlength="20" autocomplete="tel" required>
                <% if (erreurs != null && erreurs.get("telephone") != null) { %>
                    <p class="erreur"><%= escapeHtml(erreurs.get("telephone")) %></p>
                <% } %>

                <label for="adresse">Adresse</label>
                <input type="text" id="adresse" name="adresse" value="<%= escapeHtml(request.getAttribute("adresse")) %>"
                       autocomplete="street-address" required>
                <% if (erreurs != null && erreurs.get("adresse") != null) { %>
                    <p class="erreur"><%= escapeHtml(erreurs.get("adresse")) %></p>
                <% } %>

                <label for="motDePasse">Mot de passe</label>
                <input type="password" id="motDePasse" name="motDePasse"
                       minlength="6" autocomplete="new-password" required>
                <% if (erreurs != null && erreurs.get("motDePasse") != null) { %>
                    <p class="erreur"><%= escapeHtml(erreurs.get("motDePasse")) %></p>
                <% } %>

                <label for="confirmation">Confirmer le mot de passe</label>
                <input type="password" id="confirmation" name="confirmation"
                       minlength="6" autocomplete="new-password" required>
                <% if (erreurs != null && erreurs.get("confirmation") != null) { %>
                    <p class="erreur"><%= escapeHtml(erreurs.get("confirmation")) %></p>
                <% } %>

                <input type="submit" value="Créer mon compte" class="bouton-valider">
            </form>

            <a href="${pageContext.request.contextPath}/login" class="lien-fleche">
                <svg viewBox="0 0 24 24"><path d="M9 5l7 7-7 7"/></svg>
                Déjà client ? Connectez-vous
            </a>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
