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
                    <li><a href="${baseUrl}">Contrats</a></li>
                    <li><c:out value="${contrat.numContrat}"/></li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div style="display:flex;gap:18px;align-items:center">
                        <c:set var="nomType" value="${contrat.typeAssurance.nom}"/>
                        <span class="gp-icone gp-icone-grande"><%@ include file="/WEB-INF/views/commun/icone-assurance.jspf" %></span>
                        <div>
                            <h1 class="gp-titre">Contrat <c:out value="${contrat.numContrat}"/></h1>
                            <p class="gp-sous-titre" style="margin-top:6px">Assurance <c:out value="${contrat.typeAssurance.nom}"/> ·
                                <c:out value="${contrat.utilisateur.prenom} ${contrat.utilisateur.nom}"/>
                                &nbsp;<span class="gp-statut gp-statut-${contrat.statut}">${contrat.statut.libelle}</span></p>
                        </div>
                    </div>
                    <div class="gp-bandeau-actions">
                        <a href="${baseUrl}?action=form&id=${contrat.idContrat}" class="gp-bouton-contour">
                            <svg viewBox="0 0 24 24"><path d="M4 20h4L19 9l-4-4L4 16zM13.5 6.5l4 4"/></svg>
                            Modifier
                        </a>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <%@ include file="/WEB-INF/views/commun/flash.jspf" %>

            <c:if test="${contrat.statut == 'EN_ATTENTE'}">
                <div class="gp-message gp-message-alerte">
                    <svg viewBox="0 0 24 24"><path d="M12 3.5 2.5 20h19zM12 10v4.5M12 17.5h.01"/></svg>
                    <span><strong>Ce contrat attend votre validation.</strong> Tant qu'il n'est pas activé, le client n'est pas couvert et ne peut pas déclarer de sinistre.</span>
                </div>
            </c:if>

            <div class="gp-colonnes">
                <div class="gp-pile">
                    <section class="gp-carte">
                        <div class="gp-carte-entete"><h2 class="gp-carte-titre">Informations du contrat</h2></div>
                        <div class="gp-carte-corps">
                            <dl class="gp-donnees">
                                <div><dt>Titulaire</dt><dd>
                                    <a href="${pageContext.request.contextPath}/agent/clients?id=${contrat.utilisateur.idUtilisateur}" class="gp-lien">
                                        <c:out value="${contrat.utilisateur.prenom} ${contrat.utilisateur.nom}"/></a>
                                    <small><c:out value="${contrat.utilisateur.email}"/></small></dd></div>
                                <div><dt>Produit</dt><dd>Assurance <c:out value="${contrat.typeAssurance.nom}"/></dd></div>
                                <div><dt>Date d'effet</dt><dd>${WebUtil.date(contrat.dateDebut)}</dd></div>
                                <div><dt>Échéance</dt><dd>${WebUtil.date(contrat.dateFin)}</dd></div>
                                <div><dt>Prime annuelle</dt><dd>${WebUtil.euros(contrat.prime)}</dd></div>
                            </dl>
                        </div>
                    </section>

                    <section class="gp-carte">
                        <div class="gp-carte-entete">
                            <h2 class="gp-carte-titre">Garanties</h2>
                            <span class="gp-discret">${contrat.garanties.size()} incluse(s)</span>
                        </div>
                        <div class="gp-carte-corps">
                            <c:choose>
                                <c:when test="${empty contrat.garanties}">
                                    <p class="gp-discret" style="margin:0">Aucune garantie rattachée.
                                        <a href="${baseUrl}?action=form&id=${contrat.idContrat}" class="gp-lien">Ajouter des garanties</a></p>
                                </c:when>
                                <c:otherwise>
                                    <div class="gp-garanties">
                                        <c:forEach var="g" items="${contrat.garanties}">
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
                </div>

                <aside class="gp-pile gp-collant">
                    <section class="gp-carte gp-carte-corps">
                        <h2 class="gp-aside-titre">Cycle de vie</h2>
                        <p class="gp-discret" style="margin:0 0 16px">Statut actuel : <span class="gp-statut gp-statut-${contrat.statut}">${contrat.statut.libelle}</span></p>
                        <form method="post" action="${baseUrl}/action" style="display:flex;flex-direction:column;gap:10px">
                            <input type="hidden" name="id" value="${contrat.idContrat}">
                            <c:if test="${contrat.statut != 'RESILIE'}">
                                <div class="gp-champ" style="margin-bottom:4px">
                                    <label for="echeances" style="font-size:0.9rem">Échéancier généré à l'activation / au renouvellement</label>
                                    <select id="echeances" name="echeances">
                                        <option value="12">Mensuel (12 prélèvements)</option>
                                        <option value="4">Trimestriel (4 prélèvements)</option>
                                        <option value="1">Annuel (1 prélèvement)</option>
                                    </select>
                                </div>
                            </c:if>
                            <c:if test="${contrat.statut == 'EN_ATTENTE' or contrat.statut == 'SUSPENDU'}">
                                <button type="submit" name="action" value="activer" class="gp-bouton gp-bouton-bloc">
                                    <svg viewBox="0 0 24 24"><path d="m5 12.5 4.5 4.5L19 7.5"/></svg>
                                    ${contrat.statut == 'SUSPENDU' ? 'Réactiver le contrat' : 'Activer le contrat'}
                                </button>
                            </c:if>
                            <c:if test="${contrat.statut == 'ACTIF' or contrat.statut == 'EXPIRE'}">
                                <button type="submit" name="action" value="renouveler" class="gp-bouton gp-bouton-bloc"
                                        onclick="return confirm('Renouveler ce contrat pour un an ?');">Renouveler pour 1 an</button>
                            </c:if>
                            <c:if test="${contrat.statut == 'ACTIF'}">
                                <button type="submit" name="action" value="suspendre" class="gp-bouton-contour gp-bouton-bloc"
                                        onclick="return confirm('Suspendre ce contrat ?');">Suspendre</button>
                            </c:if>
                            <c:if test="${contrat.statut != 'RESILIE'}">
                                <button type="submit" name="action" value="resilier" class="gp-bouton-danger gp-bouton-bloc"
                                        onclick="return confirm('Résilier définitivement ce contrat ?');">Résilier</button>
                            </c:if>
                        </form>
                        <c:if test="${estAdmin}">
                            <hr class="gp-separateur">
                            <a href="${baseUrl}?action=delete&id=${contrat.idContrat}" class="gp-lien gp-lien-danger"
                               onclick="return confirm('Supprimer définitivement ce contrat ? Cette action est irréversible.');">Supprimer le contrat</a>
                        </c:if>
                    </section>
                </aside>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
