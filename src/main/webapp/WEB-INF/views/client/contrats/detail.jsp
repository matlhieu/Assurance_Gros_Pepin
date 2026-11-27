<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Contrat ${contrat.numContrat} – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <ol class="gp-fil">
                    <li><a href="${pageContext.request.contextPath}/client/dashboard">Mon espace</a></li>
                    <li><a href="${pageContext.request.contextPath}/client/contrats">Mes contrats</a></li>
                    <li><c:out value="${contrat.numContrat}"/></li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div style="display:flex;gap:18px;align-items:center">
                        <c:set var="nomType" value="${contrat.typeAssurance.nom}"/>
                        <span class="gp-icone gp-icone-grande"><%@ include file="/WEB-INF/views/commun/icone-assurance.jspf" %></span>
                        <div>
                            <h1 class="gp-titre">Assurance <c:out value="${contrat.typeAssurance.nom}"/></h1>
                            <p class="gp-sous-titre" style="margin-top:6px">Contrat n° <c:out value="${contrat.numContrat}"/>
                                &nbsp;<span class="gp-statut gp-statut-${contrat.statut}">${contrat.statut.libelle}</span></p>
                        </div>
                    </div>
                    <c:if test="${contrat.statut == 'ACTIF'}">
                        <div class="gp-bandeau-actions">
                            <a href="${pageContext.request.contextPath}/client/sinistres/declarer?contrat=${contrat.idContrat}" class="gp-bouton">
                                <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                                Déclarer un sinistre
                            </a>
                        </div>
                    </c:if>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <c:if test="${contrat.statut != 'ACTIF'}">
                <div class="gp-message gp-message-alerte">
                    <svg viewBox="0 0 24 24"><path d="M12 3.5 2.5 20h19zM12 10v4.5M12 17.5h.01"/></svg>
                    <span><strong>Ce contrat n'est pas actif (${fn:toLowerCase(contrat.statut.libelle)}).</strong>
                        Vous ne pouvez pas y déclarer de sinistre. Contactez votre agent pour en savoir plus.</span>
                </div>
            </c:if>

            <div class="gp-colonnes">
                <section class="gp-carte">
                    <div class="gp-carte-entete">
                        <h2 class="gp-carte-titre">Vos garanties</h2>
                        <span class="gp-discret">${contrat.garanties.size()} incluse${contrat.garanties.size() > 1 ? 's' : ''}</span>
                    </div>
                    <div class="gp-carte-corps">
                        <c:choose>
                            <c:when test="${empty contrat.garanties}">
                                <p class="gp-discret" style="margin:0">Aucune garantie n'est rattachée à ce contrat.</p>
                            </c:when>
                            <c:otherwise>
                                <div class="gp-garanties">
                                    <c:forEach var="g" items="${contrat.garanties}">
                                        <div class="gp-garantie">
                                            <span class="gp-icone"><svg viewBox="0 0 24 24"><path d="m5 12.5 4.5 4.5L19 7.5"/></svg></span>
                                            <div>
                                                <strong><c:out value="${g.nom}"/></strong>
                                                <small><c:out value="${g.description}"/></small>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </section>

                <aside class="gp-pile gp-collant">
                    <section class="gp-carte">
                        <div class="gp-carte-corps">
                            <h2 class="gp-aside-titre">Votre cotisation</h2>
                            <div class="gp-montant-fort">${WebUtil.euros(contrat.prime)}</div>
                            <p class="gp-discret" style="margin:4px 0 0">par an</p>
                            <hr class="gp-separateur">
                            <dl class="gp-donnees">
                                <div><dt>Souscrit le</dt><dd>${WebUtil.date(contrat.dateDebut)}</dd></div>
                                <div><dt>Échéance</dt><dd>${WebUtil.date(contrat.dateFin)}</dd></div>
                                <div><dt>Titulaire</dt><dd><c:out value="${contrat.utilisateur.prenom} ${contrat.utilisateur.nom}"/></dd></div>
                            </dl>
                        </div>
                        <div class="gp-carte-pied">
                            <a href="${pageContext.request.contextPath}/client/paiements" class="gp-lien">Voir mes paiements →</a>
                        </div>
                    </section>

                    <section class="gp-carte gp-carte-corps">
                        <h2 class="gp-aside-titre">Besoin d'aide ?</h2>
                        <p class="gp-discret" style="margin:0 0 14px">Une question sur vos garanties ? Notre assistant vous répond tout de suite.</p>
                        <a href="${pageContext.request.contextPath}/assistant" class="gp-bouton-clair gp-bouton-bloc">Poser une question</a>
                    </section>
                </aside>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
