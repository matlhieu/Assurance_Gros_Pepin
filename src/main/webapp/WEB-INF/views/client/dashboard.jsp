<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Mon espace – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <div class="gp-bandeau-ligne">
                    <div>
                        <p class="gp-surtitre">Espace client</p>
                        <h1 class="gp-titre">Bonjour <c:out value="${utilisateur.prenom}"/></h1>
                        <p class="gp-sous-titre">Retrouvez vos contrats, suivez vos sinistres et gérez vos paiements en un coup d'œil.</p>
                    </div>
                    <div class="gp-bandeau-actions">
                        <a href="${pageContext.request.contextPath}/client/sinistres/declarer" class="gp-bouton">
                            <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                            Déclarer un sinistre
                        </a>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">

            <%-- ===== Chiffres clés ===== --%>
            <div class="gp-grille gp-grille-3">
                <a href="${pageContext.request.contextPath}/client/contrats" class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut">
                        <span class="gp-kpi-libelle">Contrats actifs</span>
                        <span class="gp-icone"><svg viewBox="0 0 24 24"><path d="M12 3 4.5 6v6c0 4.4 3.2 8.2 7.5 9 4.3-.8 7.5-4.6 7.5-9V6z"/></svg></span>
                    </div>
                    <div>
                        <div class="gp-kpi-valeur">${contratsActifs}</div>
                        <div class="gp-kpi-note">sur ${nbContrats} contrat${nbContrats > 1 ? 's' : ''}</div>
                    </div>
                    <span class="gp-kpi-lien">Voir mes contrats →</span>
                </a>

                <a href="${pageContext.request.contextPath}/client/sinistres" class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut">
                        <span class="gp-kpi-libelle">Sinistres en cours</span>
                        <span class="gp-icone gp-icone-info"><svg viewBox="0 0 24 24"><path d="M13 2.5 4.5 14h7l-1 7.5L19 10h-7z"/></svg></span>
                    </div>
                    <div>
                        <div class="gp-kpi-valeur">${sinistresEnCours}</div>
                        <div class="gp-kpi-note">${nbSinistres} déclaré${nbSinistres > 1 ? 's' : ''} au total</div>
                    </div>
                    <span class="gp-kpi-lien">Suivre mes sinistres →</span>
                </a>

                <a href="${pageContext.request.contextPath}/client/paiements" class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut">
                        <span class="gp-kpi-libelle">Prochaine échéance</span>
                        <span class="gp-icone ${empty prochaineEcheanceDate ? 'gp-icone-ok' : 'gp-icone-alerte'}">
                            <svg viewBox="0 0 24 24"><path d="M4 6.5h16v13.5H4zM4 10.5h16M8.5 4v4M15.5 4v4"/></svg>
                        </span>
                    </div>
                    <c:choose>
                        <c:when test="${empty prochaineEcheanceDate}">
                            <div>
                                <div class="gp-kpi-valeur">À jour</div>
                                <div class="gp-kpi-note">Aucune cotisation en attente</div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div>
                                <div class="gp-kpi-valeur">${WebUtil.euros(prochaineEcheanceMontant)}</div>
                                <div class="gp-kpi-note">le ${WebUtil.date(prochaineEcheanceDate)} · ${WebUtil.euros(montantEnAttente)} en attente au total</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <span class="gp-kpi-lien">Voir mes paiements →</span>
                </a>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
