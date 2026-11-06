<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Connexion – Assurance Gros Pépin</title>
    <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png">
    <link href="https://fonts.googleapis.com/css2?family=Fredoka:wght@500;600&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
</head>
<body>
    <%-- Directive include : en-tête commun à toutes les pages --%>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="accueil">
        <section class="accueil-texte">
            <h1 class="accueil-titre">Votre assurance, sans pépins.</h1>
            <p class="accueil-intro">
                Retrouvez vos contrats, vos garanties et vos paiements au même endroit.
                Un souci&nbsp;? Déclarez votre sinistre en ligne et suivez-le jusqu’à l’indemnisation.
            </p>
            <ul class="accueil-atouts">
                <li><span class="pepin"></span>Consultez vos contrats et garanties</li>
                <li><span class="pepin"></span>Déclarez et suivez vos sinistres</li>
                <li><span class="pepin"></span>Posez vos questions à l’assistant, à toute heure</li>
            </ul>
        </section>

        <section class="carte-connexion" id="connexion">
            <div class="carte-chair">
                <h2 class="carte-titre">Connexion</h2>
                <p class="carte-sous-titre">Accédez à votre espace client, agent ou administrateur.</p>

                <%-- Message d'erreur placé par la servlet avec request.setAttribute("erreur", ...) --%>
                <% if (request.getAttribute("erreur") != null) { %>
                    <p class="alerte">${erreur}</p>
                <% } %>

                <%-- Formulaire POST envoyé à la servlet /connexion --%>
                <form method="post" action="${pageContext.request.contextPath}/connexion" class="formulaire">
                    <div class="champ">
                        <label for="email">Adresse e-mail</label>
                        <input type="email" id="email" name="email" placeholder="nom@exemple.fr" required>
                    </div>

                    <div class="champ">
                        <label for="motDePasse">Mot de passe</label>
                        <input type="password" id="motDePasse" name="motDePasse" required>
                    </div>

                    <input type="submit" value="Se connecter" class="btn-principal">
                </form>

                <p class="carte-bas">
                    Pas encore client&nbsp;?
                    <a href="${pageContext.request.contextPath}/inscription">Créer un compte</a>
                </p>
            </div>
            <div class="carte-ecorce"></div>
        </section>
    </main>
</body>
</html>
