<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Mes paiements – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <ol class="gp-fil">
                    <li><a href="${pageContext.request.contextPath}/client/dashboard">Mon espace</a></li>
                    <li>Mes paiements</li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div>
                        <h1 class="gp-titre">Mes paiements</h1>
                        <p class="gp-sous-titre">Vos prochaines échéances et l'historique de vos cotisations.</p>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <%-- ===== Chiffres clés ===== --%>
            <div class="gp-grille gp-grille-3">
                <div class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut">
                        <span class="gp-kpi-libelle">Prochaine échéance</span>
                        <span class="gp-icone ${empty prochaine ? 'gp-icone-ok' : 'gp-icone-alerte'}">
                            <svg viewBox="0 0 24 24"><path d="M4 6.5h16v13.5H4zM4 10.5h16M8.5 4v4M15.5 4v4"/></svg>
                        </span>
                    </div>
                    <c:choose>
                        <c:when test="${empty prochaine}">
                            <div>
                                <div class="gp-kpi-valeur">À jour</div>
                                <div class="gp-kpi-note">Aucune cotisation en attente</div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div>
                                <div class="gp-kpi-valeur">${WebUtil.euros(prochaine.montant)}</div>
                                <div class="gp-kpi-note">le ${WebUtil.date(prochaine.datePaiement)} · contrat <c:out value="${prochaine.contrat.numContrat}"/></div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut">
                        <span class="gp-kpi-libelle">Reste à régler</span>
                        <span class="gp-icone gp-icone-info"><svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg></span>
                    </div>
                    <div>
                        <div class="gp-kpi-valeur">${WebUtil.euros(totalEnAttente)}</div>
                        <div class="gp-kpi-note">${aRegler.size()} échéance${aRegler.size() > 1 ? 's' : ''} en attente</div>
                    </div>
                </div>

                <div class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut">
                        <span class="gp-kpi-libelle">Total réglé</span>
                        <span class="gp-icone gp-icone-ok"><svg viewBox="0 0 24 24"><path d="m5 12.5 4.5 4.5L19 7.5"/></svg></span>
                    </div>
                    <div>
                        <div class="gp-kpi-valeur">${WebUtil.euros(totalPaye)}</div>
                        <div class="gp-kpi-note">depuis l'ouverture de vos contrats</div>
                    </div>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty paiements}">
                    <div class="gp-carte gp-vide gp-section">
                        <span class="gp-icone gp-icone-grande"><svg viewBox="0 0 24 24"><path d="M3 6.5h18v11H3zM3 10.5h18M7 14.5h3"/></svg></span>
                        <p class="gp-vide-titre">Aucun paiement pour le moment</p>
                        <p>Vos échéances apparaîtront ici dès la souscription d'un contrat.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <%-- ===== À régler ===== --%>
                    <section class="gp-section">
                        <h2 class="gp-section-titre">À régler</h2>
                        <div class="gp-carte">
                            <c:choose>
                                <c:when test="${empty aRegler}">
                                    <div class="gp-vide">
                                        <span class="gp-icone gp-icone-grande gp-icone-ok"><svg viewBox="0 0 24 24"><path d="m5 12.5 4.5 4.5L19 7.5"/></svg></span>
                                        <p class="gp-vide-titre">Vous êtes à jour</p>
                                        <p>Aucune échéance en attente. Merci !</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <ul class="gp-lignes">
                                        <c:forEach var="p" items="${aRegler}">
                                            <li class="gp-ligne">
                                                <span class="gp-date">
                                                    <strong>${p.datePaiement.dayOfMonth}</strong>
                                                    <small>${WebUtil.moisAnnee(p.datePaiement)}</small>
                                                </span>
                                                <span class="gp-ligne-texte">
                                                    <span class="gp-ligne-titre">Cotisation assurance <c:out value="${p.contrat.typeAssurance.nom}"/></span>
                                                    <span class="gp-ligne-meta">Contrat <c:out value="${p.contrat.numContrat}"/> · ${WebUtil.libelle(p.modePaiement)}</span>
                                                </span>
                                                <span class="gp-ligne-montant">${WebUtil.euros(p.montant)}</span>
                                                <span class="gp-statut gp-statut-${p.statut}">${WebUtil.libelle(p.statut)}</span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                    <div class="gp-carte-pied gp-discret">
                                        Les échéances sont réglées selon le mode indiqué. Une question ? Contactez votre agent.
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </section>

                    <%-- ===== Historique ===== --%>
                    <section class="gp-section">
                        <h2 class="gp-section-titre">Historique</h2>
                        <div class="gp-carte">
                            <c:choose>
                                <c:when test="${empty historique}">
                                    <div class="gp-vide">
                                        <p class="gp-vide-titre">Aucun paiement effectué</p>
                                        <p>Vos règlements apparaîtront ici.</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="gp-tableau-boite">
                                        <table class="gp-tableau">
                                            <thead>
                                                <tr>
                                                    <th>Date</th>
                                                    <th>Contrat</th>
                                                    <th>Mode de règlement</th>
                                                    <th class="gp-num">Montant</th>
                                                    <th>Statut</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="p" items="${historique}">
                                                    <tr>
                                                        <td>${WebUtil.date(p.datePaiement)}</td>
                                                        <td><c:out value="${p.contrat.typeAssurance.nom}"/>
                                                            <small><c:out value="${p.contrat.numContrat}"/></small></td>
                                                        <td>${WebUtil.libelle(p.modePaiement)}</td>
                                                        <td class="gp-num"><strong>${WebUtil.euros(p.montant)}</strong></td>
                                                        <td><span class="gp-statut gp-statut-${p.statut}">${WebUtil.libelle(p.statut)}</span></td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </section>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
