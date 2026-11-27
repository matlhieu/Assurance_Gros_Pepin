<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Dossier ${sinistre.numeroSinistre} – Gestion des sinistres</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <ol class="gp-fil">
                    <li><a href="${baseUrl}">Sinistres</a></li>
                    <li>${sinistre.numeroSinistre}</li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div style="display:flex;gap:18px;align-items:center">
                        <c:set var="typeSin" value="${sinistre.typeSinistre}"/>
                        <span class="gp-icone gp-icone-grande"><%@ include file="/WEB-INF/views/commun/icone-sinistre.jspf" %></span>
                        <div>
                            <h1 class="gp-titre">Dossier ${sinistre.numeroSinistre}</h1>
                            <p class="gp-sous-titre" style="margin-top:6px">
                                <c:out value="${sinistre.typeSinistre}"/> · <c:out value="${sinistre.contrat.utilisateur.prenom} ${sinistre.contrat.utilisateur.nom}"/>
                                · déclaré le ${WebUtil.date(sinistre.dateDeclaration)}
                                &nbsp;<span class="gp-statut gp-statut-${sinistre.statut}">${WebUtil.libelle(sinistre.statut)}</span>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <%@ include file="/WEB-INF/views/commun/flash.jspf" %>

            <div class="gp-colonnes">
                <div class="gp-pile">

                    <%-- ===== Traitement : un seul formulaire, un bouton par décision ===== --%>
                    <c:if test="${sinistre.statut == 'DECLARE' or sinistre.statut == 'EN_ANALYSE' or sinistre.statut == 'ACCEPTE'}">
                        <section class="gp-carte">
                            <form method="post" action="${baseUrl}">
                                <input type="hidden" name="id" value="${sinistre.idSinistre}">
                                <div class="gp-form-section">
                                    <h2 class="gp-etape-titre">
                                        <span class="gp-icone" style="width:36px;height:36px"><svg viewBox="0 0 24 24"><path d="M4 20h4L19 9l-4-4L4 16zM13.5 6.5l4 4"/></svg></span>
                                        <c:choose>
                                            <c:when test="${sinistre.statut == 'DECLARE'}">Prendre en charge le dossier</c:when>
                                            <c:when test="${sinistre.statut == 'EN_ANALYSE'}">Rendre votre décision</c:when>
                                            <c:otherwise>Indemniser le client</c:otherwise>
                                        </c:choose>
                                    </h2>
                                    <p class="gp-etape-aide" style="margin-left:48px">
                                        <c:choose>
                                            <c:when test="${sinistre.statut == 'DECLARE'}">Le client verra que son dossier est à l'étude. Vous pouvez aussi lui laisser un message.</c:when>
                                            <c:when test="${sinistre.statut == 'EN_ANALYSE'}">Acceptez et indemnisez en une fois, ou refusez avec une explication. Le client est informé dans son espace.</c:when>
                                            <c:otherwise>Le sinistre est accepté : indiquez le montant versé pour clore le dossier.</c:otherwise>
                                        </c:choose>
                                    </p>

                                    <div class="gp-champ">
                                        <label for="commentaire">Message au client
                                            <span class="gp-optionnel">${sinistre.statut == 'EN_ANALYSE' ? '(obligatoire en cas de refus)' : '(facultatif)'}</span></label>
                                        <textarea id="commentaire" name="commentaire" maxlength="2000"
                                                  placeholder="Ex. : pièces reçues, expertise prévue le…, motif de la décision…"><c:out value="${sinistre.commentaireAgent}"/></textarea>
                                    </div>

                                    <c:if test="${sinistre.statut == 'EN_ANALYSE' or sinistre.statut == 'ACCEPTE'}">
                                        <div class="gp-champ">
                                            <label for="montant">Montant de l'indemnisation</label>
                                            <div class="gp-champ-montant">
                                                <input type="number" id="montant" name="montant" min="0.01" step="0.01"
                                                       max="${sinistre.montantEstime}" value="${sinistre.montantEstime}">
                                            </div>
                                            <span class="gp-aide">Pré-rempli avec l'estimation du client (${WebUtil.euros(sinistre.montantEstime)}), qui est aussi le maximum.</span>
                                        </div>
                                    </c:if>
                                </div>

                                <div class="gp-form-actions">
                                    <button type="submit" name="action" value="commenter" class="gp-bouton-contour">Enregistrer le message seulement</button>
                                    <div style="display:flex;gap:10px;flex-wrap:wrap">
                                        <c:choose>
                                            <c:when test="${sinistre.statut == 'DECLARE'}">
                                                <button type="submit" name="action" value="statut:EN_ANALYSE" class="gp-bouton">Prendre en charge</button>
                                            </c:when>
                                            <c:when test="${sinistre.statut == 'EN_ANALYSE'}">
                                                <button type="submit" name="action" value="statut:REFUSE" class="gp-bouton-danger"
                                                        onclick="return confirm('Refuser définitivement ce sinistre ?');">Refuser</button>
                                                <button type="submit" name="action" value="statut:ACCEPTE" class="gp-bouton-contour">Accepter sans indemniser</button>
                                                <button type="submit" name="action" value="accepterIndemniser" class="gp-bouton"
                                                        onclick="return confirm('Accepter et indemniser ? Le dossier sera clos.');">
                                                    <svg viewBox="0 0 24 24"><path d="m5 12.5 4.5 4.5L19 7.5"/></svg>Accepter et indemniser</button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="submit" name="action" value="indemniser" class="gp-bouton"
                                                        onclick="return confirm('Confirmer l\'indemnisation ? Le dossier sera clos.');">Valider l'indemnisation</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </form>
                        </section>
                    </c:if>

                    <c:if test="${sinistre.statut == 'REFUSE' or sinistre.statut == 'INDEMNISE'}">
                        <div class="gp-message ${sinistre.statut == 'REFUSE' ? 'gp-message-erreur' : 'gp-message-ok'}" style="margin:0">
                            <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M8 12h8"/></svg>
                            <span><strong>Dossier clos.</strong>
                                ${sinistre.statut == 'REFUSE' ? 'Le sinistre a été refusé.' : 'Le client a été indemnisé de '.concat(WebUtil.euros(sinistre.montantIndemnise)).concat('.')}
                                Aucune modification n'est possible.</span>
                        </div>
                        <c:if test="${not empty sinistre.commentaireAgent}">
                            <section class="gp-carte">
                                <div class="gp-carte-entete"><h2 class="gp-carte-titre">Message envoyé au client</h2></div>
                                <div class="gp-carte-corps"><p class="gp-texte"><c:out value="${sinistre.commentaireAgent}"/></p></div>
                            </section>
                        </c:if>
                    </c:if>

                    <%-- ===== Dossier ===== --%>
                    <section class="gp-carte">
                        <div class="gp-carte-entete"><h2 class="gp-carte-titre">Déclaration du client</h2></div>
                        <div class="gp-carte-corps"><p class="gp-texte"><c:out value="${sinistre.description}"/></p></div>
                    </section>

                    <section class="gp-carte">
                        <div class="gp-carte-entete">
                            <h2 class="gp-carte-titre">Pièces justificatives</h2>
                            <span class="gp-discret">${sinistre.piecesJointes.size()} fichier(s)</span>
                        </div>
                        <div class="gp-carte-corps">
                            <c:choose>
                                <c:when test="${empty sinistre.piecesJointes}">
                                    <p class="gp-discret" style="margin:0">Aucune pièce jointe.</p>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="pj" items="${sinistre.piecesJointes}">
                                        <c:set var="ext" value="${fn:toUpperCase(fn:substringAfter(pj.cheminFichier, '.'))}"/>
                                        <a href="${pageContext.request.contextPath}/pieces-jointes?id=${pj.idPiece}" target="_blank" rel="noopener" class="gp-piece">
                                            <span class="gp-piece-ext ${ext == 'PDF' ? '' : 'gp-piece-ext-img'}">${ext}</span>
                                            <span class="gp-piece-nom"><c:out value="${pj.nomFichier}"/></span>
                                            <span class="gp-lien">Ouvrir</span>
                                        </a>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </section>
                </div>

                <aside class="gp-pile gp-collant">
                    <section class="gp-carte gp-carte-corps">
                        <h2 class="gp-aside-titre">Montants</h2>
                        <dl class="gp-donnees">
                            <div><dt>Estimé par le client</dt><dd>${WebUtil.euros(sinistre.montantEstime)}</dd></div>
                            <div><dt>Indemnisé</dt><dd>${WebUtil.euros(sinistre.montantIndemnise)}</dd></div>
                        </dl>
                    </section>

                    <section class="gp-carte gp-carte-corps">
                        <h2 class="gp-aside-titre">Suivi</h2>
                        <%@ include file="/WEB-INF/views/commun/etapes-sinistre.jspf" %>
                    </section>

                    <section class="gp-carte gp-carte-corps">
                        <h2 class="gp-aside-titre">Client et contrat</h2>
                        <dl class="gp-donnees">
                            <div><dt>Client</dt><dd><c:out value="${sinistre.contrat.utilisateur.prenom} ${sinistre.contrat.utilisateur.nom}"/>
                                <small><c:out value="${sinistre.contrat.utilisateur.email}"/></small></dd></div>
                            <div><dt>Contrat</dt><dd><c:out value="${sinistre.contrat.numContrat}"/>
                                <small><c:out value="${sinistre.contrat.typeAssurance.nom}"/></small></dd></div>
                            <div><dt>Statut</dt><dd><span class="gp-statut gp-statut-${sinistre.contrat.statut}">${sinistre.contrat.statut.libelle}</span></dd></div>
                            <div><dt>Validité</dt><dd>${WebUtil.date(sinistre.contrat.dateDebut)}<small>au ${WebUtil.date(sinistre.contrat.dateFin)}</small></dd></div>
                        </dl>
                    </section>
                </aside>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
