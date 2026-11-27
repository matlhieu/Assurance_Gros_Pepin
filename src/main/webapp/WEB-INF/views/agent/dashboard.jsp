<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Tableau de bord – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <div class="gp-bandeau-ligne">
                    <div>
                        <p class="gp-surtitre">Espace agent</p>
                        <h1 class="gp-titre">Bonjour <c:out value="${utilisateur.prenom}"/></h1>
                        <p class="gp-sous-titre">
                            <c:choose>
                                <c:when test="${nbDeclares + nbEnAnalyse + nbAcceptes == 0}">Aucun dossier n'attend d'action de votre part.</c:when>
                                <c:otherwise>${nbDeclares + nbEnAnalyse + nbAcceptes} dossier(s) sinistre attendent une action de votre part.</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                    <div class="gp-bandeau-actions">
                        <a href="${pageContext.request.contextPath}/agent/sinistres" class="gp-bouton">Traiter les sinistres</a>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <div class="gp-grille gp-grille-4">
                <a href="${pageContext.request.contextPath}/agent/sinistres?statut=DECLARE" class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut">
                        <span class="gp-kpi-libelle">Nouveaux sinistres</span>
                        <span class="gp-icone gp-icone-alerte"><svg viewBox="0 0 24 24"><path d="M13 2.5 4.5 14h7l-1 7.5L19 10h-7z"/></svg></span>
                    </div>
                    <div>
                        <div class="gp-kpi-valeur">${nbDeclares}</div>
                        <div class="gp-kpi-note">à prendre en charge</div>
                    </div>
                </a>
                <a href="${pageContext.request.contextPath}/agent/sinistres?statut=EN_ANALYSE" class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut">
                        <span class="gp-kpi-libelle">En analyse</span>
                        <span class="gp-icone gp-icone-info"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="6.5"/><path d="m20 20-4.4-4.4"/></svg></span>
                    </div>
                    <div>
                        <div class="gp-kpi-valeur">${nbEnAnalyse}</div>
                        <div class="gp-kpi-note">décision à rendre</div>
                    </div>
                </a>
                <a href="${pageContext.request.contextPath}/agent/sinistres?statut=ACCEPTE" class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut">
                        <span class="gp-kpi-libelle">À indemniser</span>
                        <span class="gp-icone gp-icone-ok"><svg viewBox="0 0 24 24"><path d="M3 6.5h18v11H3zM3 10.5h18M7 14.5h3"/></svg></span>
                    </div>
                    <div>
                        <div class="gp-kpi-valeur">${nbAcceptes}</div>
                        <div class="gp-kpi-note">sinistres acceptés</div>
                    </div>
                </a>
                <a href="${pageContext.request.contextPath}/agent/paiements" class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut">
                        <span class="gp-kpi-libelle">Cotisations en attente</span>
                        <span class="gp-icone"><svg viewBox="0 0 24 24"><path d="M4 6.5h16v13.5H4zM4 10.5h16M8.5 4v4M15.5 4v4"/></svg></span>
                    </div>
                    <div>
                        <div class="gp-kpi-valeur">${WebUtil.euros(montantPaiementsEnAttente)}</div>
                        <div class="gp-kpi-note">${nbPaiementsEnAttente} échéance(s) à encaisser</div>
                    </div>
                </a>
            </div>

            <c:if test="${nbContratsEnAttente > 0}">
                <div class="gp-message gp-message-alerte gp-section" style="margin-bottom:0">
                    <svg viewBox="0 0 24 24"><path d="M12 3.5 2.5 20h19zM12 10v4.5M12 17.5h.01"/></svg>
                    <span><strong>${nbContratsEnAttente} contrat(s) en attente de validation.</strong> Pensez à les activer pour que vos clients soient couverts.
                        <a href="${pageContext.request.contextPath}/agent/contrats?statut=EN_ATTENTE" class="gp-lien">Voir les contrats →</a></span>
                </div>
            </c:if>

            <div class="gp-colonnes gp-section">
                <section class="gp-carte">
                    <div class="gp-carte-entete">
                        <h2 class="gp-carte-titre">File de traitement</h2>
                        <a href="${pageContext.request.contextPath}/agent/sinistres" class="gp-lien">Tous les sinistres</a>
                    </div>
                    <c:choose>
                        <c:when test="${empty sinistresATraiter}">
                            <div class="gp-vide">
                                <span class="gp-icone gp-icone-grande gp-icone-ok"><svg viewBox="0 0 24 24"><path d="m5 12.5 4.5 4.5L19 7.5"/></svg></span>
                                <p class="gp-vide-titre">Tout est à jour</p>
                                <p>Aucun dossier n'attend d'action. Beau travail !</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <ul class="gp-lignes">
                                <c:forEach var="s" items="${sinistresATraiter}">
                                    <li>
                                        <a href="${pageContext.request.contextPath}/agent/sinistres?id=${s.idSinistre}" class="gp-ligne">
                                            <c:set var="typeSin" value="${s.typeSinistre}"/>
                                            <span class="gp-icone"><%@ include file="/WEB-INF/views/commun/icone-sinistre.jspf" %></span>
                                            <span class="gp-ligne-texte">
                                                <span class="gp-ligne-titre"><c:out value="${s.contrat.utilisateur.prenom} ${s.contrat.utilisateur.nom}"/> · <c:out value="${s.typeSinistre}"/></span>
                                                <span class="gp-ligne-meta">${s.numeroSinistre} · ${WebUtil.date(s.dateDeclaration)} · <c:out value="${s.contrat.typeAssurance.nom}"/></span>
                                            </span>
                                            <span class="gp-ligne-montant">${WebUtil.euros(s.montantEstime)}</span>
                                            <span class="gp-statut gp-statut-${s.statut}">${WebUtil.libelle(s.statut)}</span>
                                            <svg class="gp-chevron" viewBox="0 0 24 24"><path d="m9 6 6 6-6 6"/></svg>
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:otherwise>
                    </c:choose>
                </section>

                <aside class="gp-pile">
                    <section class="gp-carte gp-carte-corps">
                        <h2 class="gp-aside-titre">Répartition des sinistres</h2>
                        <c:choose>
                            <c:when test="${nbSinistres == 0}">
                                <p class="gp-discret" style="margin:0">Aucun sinistre enregistré.</p>
                            </c:when>
                            <c:otherwise>
                                <div class="gp-repartition" role="img" aria-label="Répartition des ${nbSinistres} sinistres par statut">
                                    <span style="width:${nbDeclares * 100 / nbSinistres}%;background:#E0A23B"></span>
                                    <span style="width:${nbEnAnalyse * 100 / nbSinistres}%;background:#2457A6"></span>
                                    <span style="width:${nbAcceptes * 100 / nbSinistres}%;background:#2BA394"></span>
                                    <span style="width:${nbIndemnises * 100 / nbSinistres}%;background:#1F7A3A"></span>
                                    <span style="width:${nbRefuses * 100 / nbSinistres}%;background:#B3261E"></span>
                                </div>
                                <ul class="gp-legende">
                                    <li><i style="background:#E0A23B"></i>Déclarés <b>${nbDeclares}</b></li>
                                    <li><i style="background:#2457A6"></i>En analyse <b>${nbEnAnalyse}</b></li>
                                    <li><i style="background:#2BA394"></i>Acceptés <b>${nbAcceptes}</b></li>
                                    <li><i style="background:#1F7A3A"></i>Indemnisés <b>${nbIndemnises}</b></li>
                                    <li><i style="background:#B3261E"></i>Refusés <b>${nbRefuses}</b></li>
                                </ul>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <a href="${pageContext.request.contextPath}/agent/paiements" class="gp-carte gp-tuile">
                        <span class="gp-icone gp-icone-info"><svg viewBox="0 0 24 24"><path d="M3 6.5h18v11H3zM3 10.5h18M7 14.5h3"/></svg></span>
                        <span class="gp-tuile-texte">
                            <span class="gp-tuile-titre">Encaisser les paiements</span>
                            <span class="gp-tuile-desc">${nbPaiementsEnAttente} échéance(s) en attente</span>
                        </span>
                        <svg class="gp-chevron" viewBox="0 0 24 24"><path d="m9 6 6 6-6 6"/></svg>
                    </a>
                </aside>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
