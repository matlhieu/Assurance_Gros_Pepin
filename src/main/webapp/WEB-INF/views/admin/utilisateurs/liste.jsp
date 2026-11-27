<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Utilisateurs – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <div class="gp-bandeau-ligne">
                    <div>
                        <p class="gp-surtitre">Administration</p>
                        <h1 class="gp-titre">Utilisateurs</h1>
                        <p class="gp-sous-titre">Gérez les comptes et les rôles : clients, agents et administrateurs.</p>
                    </div>
                    <div class="gp-bandeau-actions">
                        <a href="${baseUrl}?action=form" class="gp-bouton">
                            <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                            Nouvel utilisateur
                        </a>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <%@ include file="/WEB-INF/views/commun/flash.jspf" %>

            <div class="gp-carte">
                <div class="gp-outils">
                    <nav class="gp-onglets" aria-label="Filtrer par rôle">
                        <a href="${baseUrl}" class="gp-onglet ${empty filtreRole ? 'gp-onglet-actif' : ''}">Tous <span class="gp-compteur">${nbTotal}</span></a>
                        <a href="${baseUrl}?role=CLIENT" class="gp-onglet ${filtreRole == 'CLIENT' ? 'gp-onglet-actif' : ''}">Clients <span class="gp-compteur">${compteurs['CLIENT']}</span></a>
                        <a href="${baseUrl}?role=AGENT" class="gp-onglet ${filtreRole == 'AGENT' ? 'gp-onglet-actif' : ''}">Agents <span class="gp-compteur">${compteurs['AGENT']}</span></a>
                        <a href="${baseUrl}?role=ADMIN" class="gp-onglet ${filtreRole == 'ADMIN' ? 'gp-onglet-actif' : ''}">Administrateurs <span class="gp-compteur">${compteurs['ADMIN']}</span></a>
                    </nav>
                    <form method="get" action="${baseUrl}" role="search">
                        <c:if test="${not empty filtreRole}"><input type="hidden" name="role" value="${filtreRole}"></c:if>
                        <label class="gp-recherche">
                            <svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="6.5"/><path d="m20 20-4.4-4.4"/></svg>
                            <span class="gp-visuellement-cache">Rechercher</span>
                            <input type="search" name="q" value="<c:out value='${param.q}'/>" placeholder="Nom, prénom, e-mail…">
                        </label>
                    </form>
                </div>

                <c:choose>
                    <c:when test="${empty utilisateurs}">
                        <div class="gp-vide">
                            <span class="gp-icone gp-icone-grande"><svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 3.6-6.5 8-6.5s8 2.5 8 6.5"/></svg></span>
                            <p class="gp-vide-titre">Aucun utilisateur</p>
                            <p>Aucun utilisateur ne correspond à ces critères.</p>
                            <c:if test="${not empty filtreRole or not empty param.q}">
                                <a href="${baseUrl}" class="gp-bouton-contour">Réinitialiser les filtres</a>
                            </c:if>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="gp-tableau-boite">
                            <table class="gp-tableau">
                                <thead>
                                    <tr>
                                        <th>Utilisateur</th>
                                        <th>N° compte</th>
                                        <th>Rôle</th>
                                        <th class="gp-actions"><span class="gp-visuellement-cache">Actions</span></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="u" items="${utilisateurs}">
                                        <tr>
                                            <td>
                                                <div style="display:flex;align-items:center;gap:12px">
                                                    <span class="gp-avatar gp-avatar-petit">${WebUtil.initiales(u.prenom, u.nom)}</span>
                                                    <div>
                                                        <span class="gp-ref"><c:out value="${u.prenom} ${u.nom}"/></span>
                                                        <small><c:out value="${u.email}"/></small>
                                                    </div>
                                                </div>
                                            </td>
                                            <td><c:out value="${u.role == 'CLIENT' ? WebUtil.numeroClient(u.idUtilisateur) : '–'}"/></td>
                                            <td><span class="gp-statut gp-statut-${u.role}">${u.role}</span></td>
                                            <td class="gp-actions">
                                                <a href="${baseUrl}?action=form&id=${u.idUtilisateur}" class="gp-lien">Gérer</a>
                                                <c:if test="${u.idUtilisateur ne idConnecte}">
                                                    <a href="${baseUrl}?action=delete&id=${u.idUtilisateur}" class="gp-lien gp-lien-danger"
                                                       onclick="return confirm('Supprimer le compte de ${fn:escapeXml(u.prenom)} ${fn:escapeXml(u.nom)} ? Cette action est définitive.');">Supprimer</a>
                                                </c:if>
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
