<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Mes garanties – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <ol class="gp-fil">
                    <li><a href="${pageContext.request.contextPath}/client/dashboard">Mon espace</a></li>
                    <li>Mes garanties</li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div>
                        <h1 class="gp-titre">Mes garanties</h1>
                        <p class="gp-sous-titre">
                            <c:choose>
                                <c:when test="${empty contratsActifs}">Vous n'avez actuellement aucun contrat actif.</c:when>
                                <c:otherwise>Vous êtes couvert par ${nbGarantiesCouvertes} garantie${nbGarantiesCouvertes > 1 ? 's' : ''} grâce à ${contratsActifs.size()} contrat${contratsActifs.size() > 1 ? 's' : ''} actif${contratsActifs.size() > 1 ? 's' : ''}.</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <c:choose>
                <c:when test="${empty contratsActifs}">
                    <div class="gp-carte gp-vide">
                        <span class="gp-icone gp-icone-grande"><svg viewBox="0 0 24 24"><path d="M12 3 4.5 6v6c0 4.4 3.2 8.2 7.5 9 4.3-.8 7.5-4.6 7.5-9V6z"/></svg></span>
                        <p class="gp-vide-titre">Aucune garantie active</p>
                        <p>Vos garanties apparaîtront ici dès qu'un de vos contrats sera actif.</p>
                        <a href="${pageContext.request.contextPath}/client/contrats" class="gp-bouton-contour">Voir mes contrats</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="c" items="${contratsActifs}">
                        <section class="gp-carte gp-couverture">
                            <div class="gp-carte-entete">
                                <div style="display:flex;align-items:center;gap:14px">
                                    <c:set var="nomType" value="${c.typeAssurance.nom}"/>
                                    <span class="gp-icone"><%@ include file="/WEB-INF/views/commun/icone-assurance.jspf" %></span>
                                    <div>
                                        <h2 class="gp-carte-titre">Assurance <c:out value="${c.typeAssurance.nom}"/></h2>
                                        <span class="gp-discret">Contrat <c:out value="${c.numContrat}"/> · jusqu'au ${WebUtil.date(c.dateFin)}</span>
                                    </div>
                                </div>
                                <a href="${pageContext.request.contextPath}/client/contrats?id=${c.idContrat}" class="gp-lien">Voir le contrat</a>
                            </div>
                            <div class="gp-carte-corps">
                                <c:choose>
                                    <c:when test="${empty c.garanties}">
                                        <p class="gp-discret" style="margin:0">Aucune garantie rattachée à ce contrat.</p>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="gp-garanties">
                                            <c:forEach var="g" items="${c.garanties}">
                                                <div class="gp-garantie">
                                                    <span class="gp-icone"><svg viewBox="0 0 24 24"><path d="m5 12.5 4.5 4.5L19 7.5"/></svg></span>
                                                    <div><strong><c:out value="${g.nom}"/></strong><small><c:out value="${g.description}"/></small></div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </section>
                    </c:forEach>

                    <c:if test="${not empty nonCouvertes}">
                        <section class="gp-section">
                            <h2 class="gp-section-titre">Pour aller plus loin</h2>
                            <div class="gp-carte">
                                <div class="gp-carte-corps">
                                    <p class="gp-discret" style="margin:0 0 18px">Ces garanties ne sont incluses dans aucun de vos contrats actifs.
                                        Votre agent peut les ajouter à votre couverture.</p>
                                    <div class="gp-garanties">
                                        <c:forEach var="g" items="${nonCouvertes}">
                                            <div class="gp-garantie gp-garantie-absente">
                                                <span class="gp-icone"><svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg></span>
                                                <div><strong><c:out value="${g.nom}"/></strong><small><c:out value="${g.description}"/></small></div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                                <div class="gp-carte-pied">
                                    <a href="${pageContext.request.contextPath}/client/assistant" class="gp-lien">Poser une question à l'assistant →</a>
                                </div>
                            </div>
                        </section>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
