<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Sinistre ${sinistre.numeroSinistre} – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <ol class="gp-fil">
                    <li><a href="${pageContext.request.contextPath}/client/dashboard">Mon espace</a></li>
                    <li><a href="${pageContext.request.contextPath}/client/sinistres">Mes sinistres</a></li>
                    <li>${sinistre.numeroSinistre}</li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div style="display:flex;gap:18px;align-items:center">
                        <c:set var="typeSin" value="${sinistre.typeSinistre}"/>
                        <span class="gp-icone gp-icone-grande"><%@ include file="/WEB-INF/views/commun/icone-sinistre.jspf" %></span>
                        <div>
                            <h1 class="gp-titre"><c:out value="${sinistre.typeSinistre}"/></h1>
                            <p class="gp-sous-titre" style="margin-top:6px">Dossier ${sinistre.numeroSinistre} · déclaré le ${WebUtil.date(sinistre.dateDeclaration)}
                                &nbsp;<span class="gp-statut gp-statut-${sinistre.statut}">${WebUtil.libelle(sinistre.statut)}</span></p>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <%@ include file="/WEB-INF/views/commun/flash.jspf" %>

            <c:choose>
                <c:when test="${sinistre.statut == 'DECLARE'}">
                    <div class="gp-message gp-message-info">
                        <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 11v5.5M12 7.5h.01"/></svg>
                        <span><strong>Votre déclaration a bien été reçue.</strong> Un agent va prendre en charge votre dossier très prochainement.</span>
                    </div>
                </c:when>
                <c:when test="${sinistre.statut == 'EN_ANALYSE'}">
                    <div class="gp-message gp-message-info">
                        <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>
                        <span><strong>Votre dossier est à l'étude.</strong> Vous serez informé ici de la décision de votre agent.</span>
                    </div>
                </c:when>
                <c:when test="${sinistre.statut == 'ACCEPTE'}">
                    <div class="gp-message gp-message-ok">
                        <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="m8 12.5 2.8 2.8L16.5 9.5"/></svg>
                        <span><strong>Bonne nouvelle : votre sinistre est pris en charge.</strong> Votre indemnisation est en cours de traitement.</span>
                    </div>
                </c:when>
                <c:when test="${sinistre.statut == 'INDEMNISE'}">
                    <div class="gp-message gp-message-ok">
                        <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="m8 12.5 2.8 2.8L16.5 9.5"/></svg>
                        <span><strong>Vous avez été indemnisé de ${WebUtil.euros(sinistre.montantIndemnise)}.</strong> Ce dossier est clos.</span>
                    </div>
                </c:when>
                <c:when test="${sinistre.statut == 'REFUSE'}">
                    <div class="gp-message gp-message-erreur">
                        <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 7.5v5.5M12 16.5h.01"/></svg>
                        <span><strong>Ce sinistre n'a pas pu être pris en charge.</strong> Consultez l'explication de votre agent ci-dessous.</span>
                    </div>
                </c:when>
            </c:choose>

            <div class="gp-colonnes">
                <div class="gp-pile">
                    <c:if test="${not empty sinistre.commentaireAgent}">
                        <section class="gp-carte">
                            <div class="gp-carte-entete"><h2 class="gp-carte-titre">Message de votre agent</h2></div>
                            <div class="gp-carte-corps"><p class="gp-texte"><c:out value="${sinistre.commentaireAgent}"/></p></div>
                        </section>
                    </c:if>

                    <section class="gp-carte">
                        <div class="gp-carte-entete"><h2 class="gp-carte-titre">Votre déclaration</h2></div>
                        <div class="gp-carte-corps"><p class="gp-texte"><c:out value="${sinistre.description}"/></p></div>
                    </section>

                    <section class="gp-carte">
                        <div class="gp-carte-entete">
                            <h2 class="gp-carte-titre">Pièces justificatives</h2>
                            <span class="gp-discret">${sinistre.piecesJointes.size()} fichier${sinistre.piecesJointes.size() > 1 ? 's' : ''}</span>
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

                            <c:if test="${sinistre.statut != 'REFUSE' and sinistre.statut != 'INDEMNISE'}">
                                <form method="post" action="${pageContext.request.contextPath}/client/sinistres/pieces"
                                      enctype="multipart/form-data" style="margin-top:16px">
                                    <input type="hidden" name="id" value="${sinistre.idSinistre}">
                                    <label class="gp-depot" style="padding:18px">
                                        <span class="gp-depot-titre">Ajouter un justificatif : <span>parcourir</span></span>
                                        <span class="gp-aide" style="margin:0">PDF, JPG ou PNG · 5 Mo maximum par fichier</span>
                                        <input type="file" name="piecesJointes" multiple required
                                               accept=".pdf,.jpg,.jpeg,.png,application/pdf,image/jpeg,image/png"
                                               onchange="if (this.files.length) this.form.submit();">
                                    </label>
                                </form>
                            </c:if>
                        </div>
                    </section>
                </div>

                <aside class="gp-pile gp-collant">
                    <section class="gp-carte gp-carte-corps">
                        <h2 class="gp-aside-titre">Suivi de votre dossier</h2>
                        <%@ include file="/WEB-INF/views/commun/etapes-sinistre.jspf" %>
                    </section>

                    <section class="gp-carte gp-carte-corps">
                        <h2 class="gp-aside-titre">Récapitulatif</h2>
                        <dl class="gp-donnees">
                            <div><dt>Montant estimé</dt><dd>${WebUtil.euros(sinistre.montantEstime)}</dd></div>
                            <div><dt>Indemnisation</dt><dd>${WebUtil.euros(sinistre.montantIndemnise)}</dd></div>
                            <div>
                                <dt>Contrat</dt>
                                <dd><a href="${pageContext.request.contextPath}/client/contrats?id=${sinistre.contrat.idContrat}" class="gp-lien">
                                        <c:out value="${sinistre.contrat.typeAssurance.nom}"/></a>
                                    <small><c:out value="${sinistre.contrat.numContrat}"/></small></dd>
                            </div>
                        </dl>
                    </section>
                </aside>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
