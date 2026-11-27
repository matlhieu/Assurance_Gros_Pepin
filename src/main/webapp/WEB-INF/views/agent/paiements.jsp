<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Paiements – Assurance Gros Pépin</title>
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
                        <h1 class="gp-titre">Paiements</h1>
                        <p class="gp-sous-titre">Encaissez les échéances et enregistrez les règlements de vos clients.</p>
                    </div>
                    <div class="gp-bandeau-actions">
                        <a href="#nouveau" class="gp-bouton">
                            <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                            Enregistrer un paiement
                        </a>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <%@ include file="/WEB-INF/views/commun/flash.jspf" %>

            <div class="gp-grille gp-grille-3">
                <a href="?statut=EN_ATTENTE" class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut"><span class="gp-kpi-libelle">À encaisser</span>
                        <span class="gp-icone gp-icone-alerte"><svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg></span></div>
                    <div><div class="gp-kpi-valeur">${WebUtil.euros(totalEnAttente)}</div>
                        <div class="gp-kpi-note">${compteurs['EN_ATTENTE']} échéance(s) en attente</div></div>
                </a>
                <a href="?statut=EN_ATTENTE" class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut"><span class="gp-kpi-libelle">En retard</span>
                        <span class="gp-icone ${enRetard > 0 ? 'gp-icone-erreur' : 'gp-icone-ok'}"><svg viewBox="0 0 24 24"><path d="M12 3.5 2.5 20h19zM12 10v4.5M12 17.5h.01"/></svg></span></div>
                    <div><div class="gp-kpi-valeur">${enRetard}</div>
                        <div class="gp-kpi-note">échéance(s) dépassée(s)</div></div>
                </a>
                <a href="?statut=EFFECTUE" class="gp-carte gp-kpi">
                    <div class="gp-kpi-haut"><span class="gp-kpi-libelle">Encaissé</span>
                        <span class="gp-icone gp-icone-ok"><svg viewBox="0 0 24 24"><path d="m5 12.5 4.5 4.5L19 7.5"/></svg></span></div>
                    <div><div class="gp-kpi-valeur">${WebUtil.euros(totalEncaisse)}</div>
                        <div class="gp-kpi-note">${compteurs['EFFECTUE']} paiement(s) effectué(s)</div></div>
                </a>
            </div>

            <%-- ===== Liste ===== --%>
            <div class="gp-carte gp-section">
                <div class="gp-outils">
                    <nav class="gp-onglets" aria-label="Filtrer par statut">
                        <a href="${pageContext.request.contextPath}/agent/paiements" class="gp-onglet ${empty filtreStatut ? 'gp-onglet-actif' : ''}">Tous <span class="gp-compteur">${nbTotal}</span></a>
                        <c:forEach var="st" items="${statuts}">
                            <a href="?statut=${st}" class="gp-onglet ${filtreStatut == st ? 'gp-onglet-actif' : ''}">
                                ${WebUtil.libelle(st)} <span class="gp-compteur">${compteurs[st]}</span>
                            </a>
                        </c:forEach>
                    </nav>
                    <form method="get" action="${pageContext.request.contextPath}/agent/paiements" role="search">
                        <c:if test="${not empty filtreStatut}"><input type="hidden" name="statut" value="${filtreStatut}"></c:if>
                        <label class="gp-recherche">
                            <svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="6.5"/><path d="m20 20-4.4-4.4"/></svg>
                            <span class="gp-visuellement-cache">Rechercher</span>
                            <input type="search" name="q" value="<c:out value='${param.q}'/>" placeholder="N° contrat, client…">
                        </label>
                    </form>
                </div>

                <c:choose>
                    <c:when test="${empty paiements}">
                        <div class="gp-vide">
                            <span class="gp-icone gp-icone-grande"><svg viewBox="0 0 24 24"><path d="M3 6.5h18v11H3zM3 10.5h18M7 14.5h3"/></svg></span>
                            <p class="gp-vide-titre">Aucun paiement</p>
                            <p>Aucun paiement ne correspond à ces critères.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="gp-tableau-boite">
                            <table class="gp-tableau">
                                <thead>
                                    <tr>
                                        <th>Échéance</th>
                                        <th>Client</th>
                                        <th>Contrat</th>
                                        <th>Mode</th>
                                        <th class="gp-num">Montant</th>
                                        <th>Statut</th>
                                        <th class="gp-actions"><span class="gp-visuellement-cache">Actions</span></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="p" items="${paiements}">
                                        <c:set var="retard" value="${p.statut == 'EN_ATTENTE' and p.datePaiement.isBefore(aujourdhui)}"/>
                                        <tr>
                                            <td>${WebUtil.date(p.datePaiement)}
                                                <c:if test="${retard}"><small style="color:var(--gp-erreur);font-weight:700">En retard</small></c:if></td>
                                            <td><a href="${pageContext.request.contextPath}/agent/clients?id=${p.contrat.utilisateur.idUtilisateur}" class="gp-lien" style="color:inherit">
                                                <c:out value="${p.contrat.utilisateur.prenom} ${p.contrat.utilisateur.nom}"/></a></td>
                                            <td><c:out value="${p.contrat.numContrat}"/><small><c:out value="${p.contrat.typeAssurance.nom}"/></small></td>
                                            <td>${WebUtil.libelle(p.modePaiement)}</td>
                                            <td class="gp-num"><strong>${WebUtil.euros(p.montant)}</strong></td>
                                            <td><span class="gp-statut gp-statut-${p.statut}">${WebUtil.libelle(p.statut)}</span></td>
                                            <td class="gp-actions">
                                                <c:if test="${p.statut == 'EN_ATTENTE'}">
                                                    <form method="post" action="${pageContext.request.contextPath}/agent/paiements" style="margin:0">
                                                        <input type="hidden" name="action" value="encaisser">
                                                        <input type="hidden" name="idPaiement" value="${p.idPaiement}">
                                                        <button type="submit" class="gp-bouton-clair gp-petit"
                                                                onclick="return confirm('Confirmer l\'encaissement de ${WebUtil.euros(p.montant)} ?');">Encaisser</button>
                                                    </form>
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

            <%-- ===== Nouveau paiement ===== --%>
            <form method="post" action="${pageContext.request.contextPath}/agent/paiements" class="gp-carte gp-section" id="nouveau">
                <input type="hidden" name="action" value="enregistrer">
                <div class="gp-form-section">
                    <h2 class="gp-etape-titre">
                        <span class="gp-icone" style="width:36px;height:36px"><svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg></span>
                        Enregistrer un paiement
                    </h2>
                    <p class="gp-etape-aide" style="margin-left:48px">Règlement reçu hors échéancier, ou nouvelle échéance à venir.</p>
                    <div class="gp-form-grille">
                        <div class="gp-champ">
                            <label for="idContrat">Contrat</label>
                            <select id="idContrat" name="idContrat" required>
                                <option value="">Choisissez un contrat</option>
                                <c:forEach var="c" items="${contrats}">
                                    <option value="${c.idContrat}">
                                        <c:out value="${c.numContrat} – ${c.utilisateur.prenom} ${c.utilisateur.nom} (${c.typeAssurance.nom})"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="gp-champ">
                            <label for="montant">Montant</label>
                            <div class="gp-champ-montant" style="max-width:none">
                                <input type="number" id="montant" name="montant" step="0.01" min="0.01" required>
                            </div>
                        </div>
                        <div class="gp-champ">
                            <label for="modePaiement">Mode de règlement</label>
                            <select id="modePaiement" name="modePaiement" required>
                                <c:forEach var="m" items="${modes}">
                                    <option value="${m}">${WebUtil.libelle(m)}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="gp-champ">
                            <label for="datePaiement">Date <span class="gp-optionnel">(aujourd'hui par défaut)</span></label>
                            <input type="date" id="datePaiement" name="datePaiement">
                        </div>
                        <div class="gp-champ">
                            <label for="statut">Statut</label>
                            <select id="statut" name="statut">
                                <option value="EFFECTUE">Effectué (déjà reçu)</option>
                                <option value="EN_ATTENTE">En attente (échéance à venir)</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="gp-form-actions">
                    <span class="gp-discret">Le client voit ce paiement dans son espace.</span>
                    <button type="submit" class="gp-bouton">Enregistrer</button>
                </div>
            </form>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
