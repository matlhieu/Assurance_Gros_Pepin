<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Mes contrats – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <ol class="gp-fil">
                    <li><a href="${pageContext.request.contextPath}/client/dashboard">Mon espace</a></li>
                    <li>Mes contrats</li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div>
                        <h1 class="gp-titre">Mes contrats</h1>
                        <p class="gp-sous-titre">Consultez vos garanties, vos échéances et le détail de chacun de vos contrats.</p>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <c:choose>
                <c:when test="${empty contrats}">
                    <div class="gp-carte gp-vide">
                        <span class="gp-icone gp-icone-grande"><svg viewBox="0 0 24 24"><path d="M12 3 4.5 6v6c0 4.4 3.2 8.2 7.5 9 4.3-.8 7.5-4.6 7.5-9V6z"/></svg></span>
                        <p class="gp-vide-titre">Vous n'avez pas encore de contrat</p>
                        <p>Contactez un agent pour être couvert dès aujourd'hui.</p>
                        <a href="${pageContext.request.contextPath}/index.jsp#offres" class="gp-bouton-contour">Découvrir nos assurances</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="gp-grille gp-grille-3">
                        <c:forEach var="c" items="${contrats}">
                            <a href="${pageContext.request.contextPath}/client/contrats?id=${c.idContrat}" class="gp-carte gp-contrat">
                                <div class="gp-contrat-entete">
                                    <c:set var="nomType" value="${c.typeAssurance.nom}"/>
                                    <span class="gp-icone"><%@ include file="/WEB-INF/views/commun/icone-assurance.jspf" %></span>
                                    <div style="flex:1;min-width:0">
                                        <p class="gp-contrat-nom"><c:out value="${c.typeAssurance.nom}"/></p>
                                        <p class="gp-contrat-num">N° <c:out value="${c.numContrat}"/></p>
                                    </div>
                                    <span class="gp-statut gp-statut-${c.statut}">${c.statut.libelle}</span>
                                </div>
                                <div class="gp-contrat-corps">
                                    <dl class="gp-donnees">
                                        <div><dt>Début</dt><dd>${WebUtil.date(c.dateDebut)}</dd></div>
                                        <div><dt>Échéance</dt><dd>${WebUtil.date(c.dateFin)}</dd></div>
                                        <div><dt>Cotisation</dt><dd>${WebUtil.euros(c.prime)} / an</dd></div>
                                        <div><dt>Garanties</dt><dd>${c.garanties.size()}</dd></div>
                                    </dl>
                                </div>
                                <div class="gp-contrat-pied">
                                    Voir le détail
                                    <svg class="gp-chevron" viewBox="0 0 24 24"><path d="m9 6 6 6-6 6"/></svg>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
