<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- Partagée par /agent/contrats et /admin/contrats : les liens utilisent ${baseUrl} --%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Contrats – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <div class="gp-bandeau-ligne">
                    <div>
                        <p class="gp-surtitre">${estAdmin ? 'Administration' : 'Gestion'}</p>
                        <h1 class="gp-titre">Contrats</h1>
                        <p class="gp-sous-titre">Souscrivez, activez et suivez les contrats de vos clients.</p>
                    </div>
                    <div class="gp-bandeau-actions">
                        <a href="${baseUrl}?action=form" class="gp-bouton">
                            <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                            Nouveau contrat
                        </a>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <%@ include file="/WEB-INF/views/commun/flash.jspf" %>

            <div class="gp-carte">
                <div class="gp-outils">
                    <nav class="gp-onglets" aria-label="Filtrer par statut">
                        <a href="${baseUrl}" class="gp-onglet ${empty filtreStatut ? 'gp-onglet-actif' : ''}">Tous <span class="gp-compteur">${nbTotal}</span></a>
                        <c:forEach var="st" items="${statuts}">
                            <a href="${baseUrl}?statut=${st}" class="gp-onglet ${filtreStatut == st.toString() ? 'gp-onglet-actif' : ''}">
                                ${st.libelle} <span class="gp-compteur">${compteurs[st.toString()]}</span>
                            </a>
                        </c:forEach>
                    </nav>
                    <form method="get" action="${baseUrl}" role="search">
                        <c:if test="${not empty filtreStatut}"><input type="hidden" name="statut" value="${filtreStatut}"></c:if>
                        <label class="gp-recherche">
                            <svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="6.5"/><path d="m20 20-4.4-4.4"/></svg>
                            <span class="gp-visuellement-cache">Rechercher</span>
                            <input type="search" name="q" value="<c:out value='${param.q}'/>" placeholder="N° contrat, client, produit…">
                        </label>
                    </form>
                </div>

                <c:choose>
                    <c:when test="${empty contrats}">
                        <div class="gp-vide">
                            <span class="gp-icone gp-icone-grande"><svg viewBox="0 0 24 24"><path d="M6 3h9l4 4v14H6zM14 3v5h5"/></svg></span>
                            <p class="gp-vide-titre">Aucun contrat</p>
                            <p>Aucun contrat ne correspond à ces critères.</p>
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
                                        <th>Contrat</th>
                                        <th>Client</th>
                                        <th>Période</th>
                                        <th class="gp-num">Prime</th>
                                        <th>Statut</th>
                                        <th class="gp-actions"><span class="gp-visuellement-cache">Actions</span></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="c" items="${contrats}">
                                        <tr>
                                            <td>
                                                <div style="display:flex;align-items:center;gap:12px">
                                                    <c:set var="nomType" value="${c.typeAssurance.nom}"/>
                                                    <span class="gp-icone" style="width:38px;height:38px"><%@ include file="/WEB-INF/views/commun/icone-assurance.jspf" %></span>
                                                    <div>
                                                        <span class="gp-ref"><c:out value="${c.numContrat}"/></span>
                                                        <small><c:out value="${c.typeAssurance.nom}"/></small>
                                                    </div>
                                                </div>
                                            </td>
                                            <td><c:out value="${c.utilisateur.prenom} ${c.utilisateur.nom}"/>
                                                <small><c:out value="${c.utilisateur.email}"/></small></td>
                                            <td>${WebUtil.date(c.dateDebut)}<small>au ${WebUtil.date(c.dateFin)}</small></td>
                                            <td class="gp-num">${WebUtil.euros(c.prime)}</td>
                                            <td><span class="gp-statut gp-statut-${c.statut}">${c.statut.libelle}</span></td>
                                            <td class="gp-actions">
                                                <a href="${baseUrl}?action=detail&id=${c.idContrat}" class="${c.statut == 'EN_ATTENTE' ? 'gp-bouton-clair gp-petit' : 'gp-lien'}">
                                                    ${c.statut == 'EN_ATTENTE' ? 'Valider' : 'Gérer'}
                                                </a>
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
