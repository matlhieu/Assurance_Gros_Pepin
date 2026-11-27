<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title><c:out value="${client.prenom} ${client.nom}"/> – Clients</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <ol class="gp-fil">
                    <li><a href="${pageContext.request.contextPath}/agent/clients">Clients</a></li>
                    <li><c:out value="${client.prenom} ${client.nom}"/></li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div style="display:flex;gap:18px;align-items:center">
                        <span class="gp-avatar">${WebUtil.initiales(client.prenom, client.nom)}</span>
                        <div>
                            <h1 class="gp-titre"><c:out value="${client.prenom} ${client.nom}"/></h1>
                            <p class="gp-sous-titre" style="margin-top:6px">Client n° ${WebUtil.numeroClient(client.idUtilisateur)} · <c:out value="${client.email}"/></p>
                        </div>
                    </div>
                    <div class="gp-bandeau-actions">
                        <a href="mailto:<c:out value='${client.email}'/>" class="gp-bouton-contour">
                            <svg viewBox="0 0 24 24"><path d="M3.5 5.5h17v13h-17zM3.5 6l8.5 7 8.5-7"/></svg>
                            Écrire au client
                        </a>
                        <a href="${pageContext.request.contextPath}/agent/contrats?action=form&client=${client.idUtilisateur}" class="gp-bouton">
                            <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                            Nouveau contrat
                        </a>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <div class="gp-grille gp-grille-3">
                <div class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut"><span class="gp-kpi-libelle">Contrats</span>
                        <span class="gp-icone"><svg viewBox="0 0 24 24"><path d="M12 3 4.5 6v6c0 4.4 3.2 8.2 7.5 9 4.3-.8 7.5-4.6 7.5-9V6z"/></svg></span></div>
                    <div><div class="gp-kpi-valeur">${contrats.size()}</div><div class="gp-kpi-note">souscrit(s)</div></div>
                </div>
                <div class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut"><span class="gp-kpi-libelle">Sinistres</span>
                        <span class="gp-icone gp-icone-info"><svg viewBox="0 0 24 24"><path d="M13 2.5 4.5 14h7l-1 7.5L19 10h-7z"/></svg></span></div>
                    <div><div class="gp-kpi-valeur">${sinistres.size()}</div><div class="gp-kpi-note">déclaré(s)</div></div>
                </div>
                <div class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut"><span class="gp-kpi-libelle">Reste à régler</span>
                        <span class="gp-icone ${totalEnAttente.signum() > 0 ? 'gp-icone-alerte' : 'gp-icone-ok'}"><svg viewBox="0 0 24 24"><path d="M3 6.5h18v11H3zM3 10.5h18M7 14.5h3"/></svg></span></div>
                    <div><div class="gp-kpi-valeur">${WebUtil.euros(totalEnAttente)}</div><div class="gp-kpi-note">${WebUtil.euros(totalPaye)} déjà réglés</div></div>
                </div>
            </div>

            <section class="gp-section">
                <h2 class="gp-section-titre">Contrats</h2>
                <div class="gp-carte">
                    <c:choose>
                        <c:when test="${empty contrats}">
                            <div class="gp-vide">
                                <p class="gp-vide-titre">Aucun contrat</p>
                                <p>Ce client n'a encore souscrit aucun contrat.</p>
                                <a href="${pageContext.request.contextPath}/agent/contrats?action=form&client=${client.idUtilisateur}" class="gp-bouton">Créer un contrat</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <ul class="gp-lignes">
                                <c:forEach var="c" items="${contrats}">
                                    <li>
                                        <a href="${pageContext.request.contextPath}/agent/contrats?action=detail&id=${c.idContrat}" class="gp-ligne">
                                            <c:set var="nomType" value="${c.typeAssurance.nom}"/>
                                            <span class="gp-icone"><%@ include file="/WEB-INF/views/commun/icone-assurance.jspf" %></span>
                                            <span class="gp-ligne-texte">
                                                <span class="gp-ligne-titre">Assurance <c:out value="${c.typeAssurance.nom}"/></span>
                                                <span class="gp-ligne-meta"><c:out value="${c.numContrat}"/> · ${WebUtil.date(c.dateDebut)} → ${WebUtil.date(c.dateFin)} · ${c.garanties.size()} garantie(s)</span>
                                            </span>
                                            <span class="gp-ligne-montant">${WebUtil.euros(c.prime)} / an</span>
                                            <span class="gp-statut gp-statut-${c.statut}">${c.statut.libelle}</span>
                                            <svg class="gp-chevron" viewBox="0 0 24 24"><path d="m9 6 6 6-6 6"/></svg>
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>

            <div class="gp-grille gp-grille-2 gp-section">
                <section>
                    <h2 class="gp-section-titre">Sinistres</h2>
                    <div class="gp-carte">
                        <c:choose>
                            <c:when test="${empty sinistres}">
                                <div class="gp-vide"><p class="gp-vide-titre">Aucun sinistre</p><p>Ce client n'a déclaré aucun sinistre.</p></div>
                            </c:when>
                            <c:otherwise>
                                <ul class="gp-lignes">
                                    <c:forEach var="s" items="${sinistres}">
                                        <li>
                                            <a href="${pageContext.request.contextPath}/agent/sinistres?id=${s.idSinistre}" class="gp-ligne">
                                                <span class="gp-ligne-texte">
                                                    <span class="gp-ligne-titre"><c:out value="${s.typeSinistre}"/></span>
                                                    <span class="gp-ligne-meta">${s.numeroSinistre} · ${WebUtil.date(s.dateDeclaration)} · ${WebUtil.euros(s.montantEstime)}</span>
                                                </span>
                                                <span class="gp-statut gp-statut-${s.statut}">${WebUtil.libelle(s.statut)}</span>
                                            </a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </section>

                <section>
                    <h2 class="gp-section-titre">
                        Paiements
                        <c:url var="lienPaiements" value="/agent/paiements"><c:param name="q" value="${client.nom}"/></c:url>
                        <a href="${lienPaiements}" class="gp-lien">Gérer</a>
                    </h2>
                    <div class="gp-carte">
                        <c:choose>
                            <c:when test="${empty paiements}">
                                <div class="gp-vide"><p class="gp-vide-titre">Aucun paiement</p><p>Aucune échéance enregistrée.</p></div>
                            </c:when>
                            <c:otherwise>
                                <ul class="gp-lignes">
                                    <c:forEach var="p" items="${paiements}" end="5">
                                        <li class="gp-ligne">
                                            <span class="gp-ligne-texte">
                                                <span class="gp-ligne-titre">${WebUtil.euros(p.montant)}</span>
                                                <span class="gp-ligne-meta">${WebUtil.date(p.datePaiement)} · <c:out value="${p.contrat.numContrat}"/> · ${WebUtil.libelle(p.modePaiement)}</span>
                                            </span>
                                            <span class="gp-statut gp-statut-${p.statut}">${WebUtil.libelle(p.statut)}</span>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </section>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
