<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Mes sinistres – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <ol class="gp-fil">
                    <li><a href="${pageContext.request.contextPath}/client/dashboard">Mon espace</a></li>
                    <li>Mes sinistres</li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div>
                        <h1 class="gp-titre">Mes sinistres</h1>
                        <p class="gp-sous-titre">Suivez l'avancement de vos déclarations, étape par étape.</p>
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
            <%@ include file="/WEB-INF/views/commun/flash.jspf" %>

            <div class="gp-carte">
                <c:choose>
                    <c:when test="${empty sinistres}">
                        <div class="gp-vide">
                            <span class="gp-icone gp-icone-grande gp-icone-ok"><svg viewBox="0 0 24 24"><path d="m5 12.5 4.5 4.5L19 7.5"/></svg></span>
                            <p class="gp-vide-titre">Aucun sinistre déclaré</p>
                            <p>En cas de pépin, déclarez-le en ligne : c'est rapide, et vous suivez son traitement ici.</p>
                            <a href="${pageContext.request.contextPath}/client/sinistres/declarer" class="gp-bouton">Déclarer un sinistre</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="gp-carte-entete">
                            <h2 class="gp-carte-titre">${sinistres.size()} dossier${sinistres.size() > 1 ? 's' : ''}</h2>
                        </div>
                        <ul class="gp-lignes">
                            <c:forEach var="s" items="${sinistres}">
                                <li>
                                    <a href="${pageContext.request.contextPath}/client/sinistres?id=${s.idSinistre}" class="gp-ligne">
                                        <c:set var="typeSin" value="${s.typeSinistre}"/>
                                        <span class="gp-icone"><%@ include file="/WEB-INF/views/commun/icone-sinistre.jspf" %></span>
                                        <span class="gp-ligne-texte">
                                            <span class="gp-ligne-titre"><c:out value="${s.typeSinistre}"/> · Assurance <c:out value="${s.contrat.typeAssurance.nom}"/></span>
                                            <span class="gp-ligne-meta">Dossier ${s.numeroSinistre} · déclaré le ${WebUtil.date(s.dateDeclaration)} · contrat <c:out value="${s.contrat.numContrat}"/></span>
                                        </span>
                                        <span style="text-align:right">
                                            <span class="gp-ligne-montant">${WebUtil.euros(empty s.montantIndemnise ? s.montantEstime : s.montantIndemnise)}</span>
                                            <span class="gp-ligne-meta">${empty s.montantIndemnise ? 'estimé' : 'indemnisé'}</span>
                                        </span>
                                        <span class="gp-statut gp-statut-${s.statut}">${WebUtil.libelle(s.statut)}</span>
                                        <svg class="gp-chevron" viewBox="0 0 24 24"><path d="m9 6 6 6-6 6"/></svg>
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
