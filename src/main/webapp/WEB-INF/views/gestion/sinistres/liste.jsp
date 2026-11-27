<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- Partagée par /agent/sinistres et /admin/sinistres : les liens utilisent ${baseUrl} --%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Sinistres – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <div class="gp-bandeau-ligne">
                    <div>
                        <p class="gp-surtitre">Gestion</p>
                        <h1 class="gp-titre">Sinistres</h1>
                        <p class="gp-sous-titre">Prenez en charge les déclarations, rendez vos décisions et indemnisez vos clients.</p>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <%@ include file="/WEB-INF/views/commun/flash.jspf" %>

            <div class="gp-carte">
                <div class="gp-outils">
                    <nav class="gp-onglets" aria-label="Filtrer par statut">
                        <a href="${baseUrl}" class="gp-onglet ${empty filtreStatut ? 'gp-onglet-actif' : ''}">
                            Tous <span class="gp-compteur">${nbTotal}</span>
                        </a>
                        <c:forEach var="st" items="${statuts}">
                            <a href="${baseUrl}?statut=${st}" class="gp-onglet ${filtreStatut == st ? 'gp-onglet-actif' : ''}">
                                ${WebUtil.libelle(st)} <span class="gp-compteur">${compteurs[st]}</span>
                            </a>
                        </c:forEach>
                    </nav>
                    <form method="get" action="${baseUrl}" role="search">
                        <c:if test="${not empty filtreStatut}"><input type="hidden" name="statut" value="${filtreStatut}"></c:if>
                        <label class="gp-recherche">
                            <svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="6.5"/><path d="m20 20-4.4-4.4"/></svg>
                            <span class="gp-visuellement-cache">Rechercher</span>
                            <input type="search" name="q" value="<c:out value='${param.q}'/>" placeholder="N° dossier, contrat, client…">
                        </label>
                    </form>
                </div>

                <c:choose>
                    <c:when test="${empty sinistres}">
                        <div class="gp-vide">
                            <span class="gp-icone gp-icone-grande"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="6.5"/><path d="m20 20-4.4-4.4"/></svg></span>
                            <p class="gp-vide-titre">Aucun dossier</p>
                            <p>Aucun sinistre ne correspond à ces critères.</p>
                            <c:if test="${not empty filtreStatut or not empty param.q}">
                                <a href="${baseUrl}" class="gp-bouton-contour">Réinitialiser les filtres</a>
                            </c:if>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="gp-tableau-boite">
                            <table class="gp-tableau">
                                <thead>
                                    <tr>
                                        <th>Dossier</th>
                                        <th>Client</th>
                                        <th>Nature</th>
                                        <th>Déclaré le</th>
                                        <th class="gp-num">Estimé</th>
                                        <th class="gp-num">Indemnisé</th>
                                        <th>Statut</th>
                                        <th class="gp-actions"><span class="gp-visuellement-cache">Actions</span></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="s" items="${sinistres}">
                                        <tr>
                                            <td><span class="gp-ref">${s.numeroSinistre}</span>
                                                <small>Contrat <c:out value="${s.contrat.numContrat}"/></small></td>
                                            <td><c:out value="${s.contrat.utilisateur.prenom} ${s.contrat.utilisateur.nom}"/>
                                                <small><c:out value="${s.contrat.typeAssurance.nom}"/></small></td>
                                            <td><c:out value="${s.typeSinistre}"/></td>
                                            <td>${WebUtil.date(s.dateDeclaration)}</td>
                                            <td class="gp-num">${WebUtil.euros(s.montantEstime)}</td>
                                            <td class="gp-num">${WebUtil.euros(s.montantIndemnise)}</td>
                                            <td><span class="gp-statut gp-statut-${s.statut}">${WebUtil.libelle(s.statut)}</span></td>
                                            <td class="gp-actions">
                                                <c:choose>
                                                    <c:when test="${s.statut == 'REFUSE' or s.statut == 'INDEMNISE'}">
                                                        <a href="${baseUrl}?id=${s.idSinistre}" class="gp-lien">Consulter</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${baseUrl}?id=${s.idSinistre}" class="gp-bouton-clair gp-petit">Traiter</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
