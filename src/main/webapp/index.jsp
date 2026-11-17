<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Assurance Gros Pépin</title>
    <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png">
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=4">
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="accueil">
        <!-- Formes décoratives aux couleurs de la palette -->
        <div class="deco" aria-hidden="true">
            <span class="deco-1"></span>
            <span class="deco-2"></span>
            <img src="${pageContext.request.contextPath}/image/logo.png" alt="" class="deco-logo">
        </div>

        <div class="accueil-contenu">
            <h1 class="accueil-titre">
                Chez <strong>Gros Pépin</strong>, vos pépins deviennent <strong>les nôtres</strong>
            </h1>

            <section class="bloc-offres">
                <h2><strong>Nos assurances</strong> en ligne</h2>

                <div class="grille-offres">
                    <a href="#" class="offre">
                        <svg viewBox="0 0 24 24"><path d="M5 11 7 6h10l2 5"/><rect x="3" y="11" width="18" height="6" rx="2"/><path d="M5 17v2M19 17v2"/><circle cx="7.5" cy="14" r="1"/><circle cx="16.5" cy="14" r="1"/></svg>
                        <span>Assurance automobile</span>
                    </a>
                    <a href="#" class="offre">
                        <svg viewBox="0 0 24 24"><path d="M3 11 12 3l9 8M5 9.5V21h14V9.5M10 21v-6h4v6"/></svg>
                        <span>Assurance habitation</span>
                    </a>
                    <a href="#" class="offre">
                        <svg viewBox="0 0 24 24"><path d="M12 20s-7-4.4-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.6-7 10-7 10z"/></svg>
                        <span>Assurance santé</span>
                    </a>
                    <a href="#" class="offre">
                        <svg viewBox="0 0 24 24"><path d="M10 14 3 11l1.5-1.5 6 1L15 6a2 2 0 0 1 3 3l-4.5 4.5 1 6L13 21l-3-7z"/></svg>
                        <span>Assurance voyage</span>
                    </a>
                    <a href="#" class="offre">
                        <svg viewBox="0 0 24 24"><rect x="3" y="7" width="18" height="13" rx="2"/><path d="M9 7V5h6v2M3 12h18"/></svg>
                        <span>Assurance professionnelle</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/login" class="offre offre-mise-en-avant">
                        <strong>Un sinistre ?</strong>
                        <span>Déclarez-le en ligne depuis votre espace</span>
                        <svg class="fleche" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><path d="M8 12h8M13 8l4 4-4 4"/></svg>
                    </a>
                </div>
            </section>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
