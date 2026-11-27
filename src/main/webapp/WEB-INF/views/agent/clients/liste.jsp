<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Clients – Assurance Gros Pépin</title>
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
                        <h1 class="gp-titre">Clients</h1>
                        <p class="gp-sous-titre">${nbClients} client${nbClients > 1 ? 's' : ''} dans votre portefeuille.</p>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <div class="gp-carte">
                <div class="gp-outils">
                    <span class="gp-discret">
                        <c:choose>
                            <c:when test="${not empty param.q}">${lignes.size()} résultat(s) pour « <c:out value="${param.q}"/> »</c:when>
                            <c:otherwise>Tous les clients</c:otherwise>
                        </c:choose>
                    </span>
                    <form method="get" action="${pageContext.request.contextPath}/agent/clients" role="search">
                        <label class="gp-recherche">
                            <svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="6.5"/><path d="m20 20-4.4-4.4"/></svg>
                            <span class="gp-visuellement-cache">Rechercher</span>
                            <input type="search" name="q" value="<c:out value='${param.q}'/>" placeholder="Nom, prénom, e-mail…">
                        </label>
                    </form>
                </div>

                <c:choose>
                    <c:when test="${empty lignes}">
                        <div class="gp-vide">
                            <span class="gp-icone gp-icone-grande"><svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 3.6-6.5 8-6.5s8 2.5 8 6.5"/></svg></span>
                            <p class="gp-vide-titre">Aucun client</p>
                            <p>Aucun client ne correspond à cette recherche.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="gp-tableau-boite">
                            <table class="gp-tableau">
                                <thead>
                                    <tr>
                                        <th>Client</th>
                                        <th>N° client</th>
                                        <th class="gp-num">Contrats</th>
                                        <th class="gp-num">Sinistres en cours</th>
                                        <th class="gp-actions"><span class="gp-visuellement-cache">Actions</span></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="l" items="${lignes}">
                                        <tr>
                                            <td>
                                                <div style="display:flex;align-items:center;gap:12px">
                                                    <span class="gp-avatar gp-avatar-petit">${WebUtil.initiales(l.client.prenom, l.client.nom)}</span>
                                                    <div>
                                                        <strong><c:out value="${l.client.prenom} ${l.client.nom}"/></strong>
                                                        <small><c:out value="${l.client.email}"/></small>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="gp-ref">${WebUtil.numeroClient(l.client.idUtilisateur)}</td>
                                            <td class="gp-num">${l.nbActifs} actif${l.nbActifs > 1 ? 's' : ''}<small>sur ${l.nbContrats}</small></td>
                                            <td class="gp-num">
                                                <c:choose>
                                                    <c:when test="${l.nbSinistresEnCours > 0}"><span class="gp-statut gp-statut-EN_ANALYSE">${l.nbSinistresEnCours}</span></c:when>
                                                    <c:otherwise><span class="gp-discret">–</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="gp-actions">
                                                <a href="${pageContext.request.contextPath}/agent/clients?id=${l.client.idUtilisateur}" class="gp-lien">Voir la fiche</a>
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
