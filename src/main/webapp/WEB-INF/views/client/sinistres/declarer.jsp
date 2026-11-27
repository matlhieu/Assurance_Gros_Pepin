<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- Contrat présélectionné : valeur renvoyée après une erreur, sinon ?contrat=.. depuis la fiche contrat --%>
<c:set var="contratChoisi" value="${not empty param.idContrat ? param.idContrat : param.contrat}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Déclarer un sinistre – Assurance Gros Pépin</title>
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
                    <li>Déclarer un sinistre</li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div>
                        <h1 class="gp-titre">Déclarer un sinistre</h1>
                        <p class="gp-sous-titre">Quelques minutes suffisent. Munissez-vous de vos justificatifs : photos, factures, constat…</p>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <c:if test="${not empty erreurs.global}">
                <div class="gp-message gp-message-erreur">
                    <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 7.5v5.5M12 16.5h.01"/></svg>
                    <span><c:out value="${erreurs.global}"/></span>
                </div>
            </c:if>
            <c:if test="${not empty erreurs and empty erreurs.global}">
                <div class="gp-message gp-message-erreur">
                    <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 7.5v5.5M12 16.5h.01"/></svg>
                    <span><strong>Votre déclaration est incomplète.</strong> Corrigez les champs signalés ci-dessous.</span>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty contratsActifs}">
                    <div class="gp-carte gp-vide">
                        <span class="gp-icone gp-icone-grande gp-icone-alerte"><svg viewBox="0 0 24 24"><path d="M12 3.5 2.5 20h19zM12 10v4.5M12 17.5h.01"/></svg></span>
                        <p class="gp-vide-titre">Aucun contrat actif</p>
                        <p>Un sinistre ne peut être déclaré que sur un contrat actif. Contactez votre agent si vous pensez qu'il s'agit d'une erreur.</p>
                        <a href="${pageContext.request.contextPath}/client/contrats" class="gp-bouton-contour">Voir mes contrats</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="gp-colonnes">
                        <form method="post" action="${pageContext.request.contextPath}/client/sinistres/declarer"
                              enctype="multipart/form-data" class="gp-carte">

                            <%-- 1. Contrat --%>
                            <div class="gp-form-section">
                                <h2 class="gp-etape-titre"><span class="gp-etape-num">1</span> Contrat concerné</h2>
                                <p class="gp-etape-aide">Choisissez le contrat qui couvre ce sinistre.</p>
                                <div class="${not empty erreurs.idContrat ? 'gp-champ-invalide' : ''}">
                                    <fieldset class="gp-choix gp-choix-large">
                                        <legend class="gp-visuellement-cache">Contrat concerné</legend>
                                        <c:forEach var="c" items="${contratsActifs}" varStatus="v">
                                            <label class="gp-choix-option">
                                                <input type="radio" name="idContrat" value="${c.idContrat}" required
                                                       ${contratChoisi eq c.idContrat.toString() or fn:length(contratsActifs) == 1 ? 'checked' : ''}>
                                                <span class="gp-choix-carte">
                                                    <c:set var="nomType" value="${c.typeAssurance.nom}"/>
                                                    <span class="gp-icone"><%@ include file="/WEB-INF/views/commun/icone-assurance.jspf" %></span>
                                                    <span class="gp-choix-texte">
                                                        <strong><c:out value="${c.typeAssurance.nom}"/></strong>
                                                        <small>N° <c:out value="${c.numContrat}"/></small>
                                                    </span>
                                                    <span class="gp-choix-coche"></span>
                                                </span>
                                            </label>
                                        </c:forEach>
                                    </fieldset>
                                    <c:if test="${not empty erreurs.idContrat}"><p class="gp-erreur"><c:out value="${erreurs.idContrat}"/></p></c:if>
                                </div>
                            </div>

                            <%-- 2. Nature --%>
                            <div class="gp-form-section">
                                <h2 class="gp-etape-titre"><span class="gp-etape-num">2</span> Nature du sinistre</h2>
                                <p class="gp-etape-aide">Que s'est-il passé ?</p>
                                <div class="${not empty erreurs.typeSinistre ? 'gp-champ-invalide' : ''}">
                                    <fieldset class="gp-choix">
                                        <legend class="gp-visuellement-cache">Type de sinistre</legend>
                                        <c:forEach var="t" items="${typesSinistre}">
                                            <label class="gp-choix-option">
                                                <input type="radio" name="typeSinistre" value="<c:out value='${t}'/>" required ${param.typeSinistre == t ? 'checked' : ''}>
                                                <span class="gp-choix-carte">
                                                    <c:set var="typeSin" value="${t}"/>
                                                    <span class="gp-icone"><%@ include file="/WEB-INF/views/commun/icone-sinistre.jspf" %></span>
                                                    <span class="gp-choix-texte"><strong><c:out value="${t}"/></strong></span>
                                                </span>
                                            </label>
                                        </c:forEach>
                                    </fieldset>
                                    <c:if test="${not empty erreurs.typeSinistre}"><p class="gp-erreur"><c:out value="${erreurs.typeSinistre}"/></p></c:if>
                                </div>
                            </div>

                            <%-- 3. Circonstances --%>
                            <div class="gp-form-section">
                                <h2 class="gp-etape-titre"><span class="gp-etape-num">3</span> Circonstances</h2>
                                <p class="gp-etape-aide">Plus votre description est précise, plus le traitement sera rapide.</p>

                                <div class="gp-champ ${not empty erreurs.description ? 'gp-champ-invalide' : ''}">
                                    <label for="description">Description des faits</label>
                                    <textarea id="description" name="description" maxlength="2000" minlength="10" required
                                              placeholder="Date et lieu, déroulement des faits, dégâts constatés, personnes impliquées…"><c:out value="${param.description}"/></textarea>
                                    <span class="gp-aide">2 000 caractères maximum.</span>
                                    <c:if test="${not empty erreurs.description}"><p class="gp-erreur"><c:out value="${erreurs.description}"/></p></c:if>
                                </div>

                                <div class="gp-champ ${not empty erreurs.montantEstime ? 'gp-champ-invalide' : ''}">
                                    <label for="montantEstime">Montant estimé des dommages</label>
                                    <div class="gp-champ-montant">
                                        <input type="number" id="montantEstime" name="montantEstime" min="0.01" step="0.01" required
                                               inputmode="decimal" value="<c:out value='${param.montantEstime}'/>">
                                    </div>
                                    <span class="gp-aide">Une estimation suffit : votre agent la vérifiera.</span>
                                    <c:if test="${not empty erreurs.montantEstime}"><p class="gp-erreur"><c:out value="${erreurs.montantEstime}"/></p></c:if>
                                </div>
                            </div>

                            <%-- 4. Justificatifs --%>
                            <div class="gp-form-section">
                                <h2 class="gp-etape-titre"><span class="gp-etape-num">4</span> Justificatifs</h2>
                                <p class="gp-etape-aide">Photos des dégâts, factures, constat amiable, dépôt de plainte…</p>

                                <div class="gp-champ ${not empty erreurs.piecesJointes ? 'gp-champ-invalide' : ''}">
                                    <label class="gp-depot" id="depot">
                                        <span class="gp-icone gp-icone-grande"><svg viewBox="0 0 24 24"><path d="M12 15.5V4M7.5 8.5 12 4l4.5 4.5M4.5 15v4.5h15V15"/></svg></span>
                                        <span class="gp-depot-titre">Glissez vos fichiers ici ou <span>parcourez</span></span>
                                        <span class="gp-aide" style="margin:0">PDF, JPG ou PNG · 5 Mo maximum par fichier · 5 fichiers maximum</span>
                                        <input type="file" id="piecesJointes" name="piecesJointes" multiple required
                                               accept=".pdf,.jpg,.jpeg,.png,application/pdf,image/jpeg,image/png">
                                    </label>
                                    <ul class="gp-fichiers" id="listeFichiers"></ul>
                                    <c:if test="${not empty erreurs}">
                                        <span class="gp-aide">Pour des raisons de sécurité, vos fichiers doivent être sélectionnés à nouveau.</span>
                                    </c:if>
                                    <c:if test="${not empty erreurs.piecesJointes}"><p class="gp-erreur"><c:out value="${erreurs.piecesJointes}"/></p></c:if>
                                </div>
                            </div>

                            <div class="gp-form-actions">
                                <a href="${pageContext.request.contextPath}/client/sinistres" class="gp-bouton-contour">Annuler</a>
                                <button type="submit" class="gp-bouton">
                                    Envoyer ma déclaration
                                    <svg viewBox="0 0 24 24"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
                                </button>
                            </div>
                        </form>

                        <aside class="gp-pile gp-collant">
                            <section class="gp-carte gp-carte-corps">
                                <h2 class="gp-aside-titre">Comment ça se passe ?</h2>
                                <ol class="gp-puces-etapes">
                                    <li><strong>Déclaration</strong>Vous décrivez le sinistre et joignez vos justificatifs.</li>
                                    <li><strong>Analyse</strong>Un agent étudie votre dossier.</li>
                                    <li><strong>Décision</strong>Vous êtes informé de la prise en charge.</li>
                                    <li><strong>Indemnisation</strong>Le remboursement est versé, dans la limite du montant estimé.</li>
                                </ol>
                            </section>
                            <section class="gp-message gp-message-info" style="margin-bottom:0">
                                <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 11v5.5M12 7.5h.01"/></svg>
                                <span><strong>Bon à savoir</strong>En cas de vol, joignez votre dépôt de plainte. Pour un accident, le constat amiable accélère le traitement.</span>
                            </section>
                        </aside>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>

    <script>
        // Affiche la liste des fichiers choisis sous la zone de dépôt
        (function () {
            var input = document.getElementById('piecesJointes');
            var depot = document.getElementById('depot');
            var liste = document.getElementById('listeFichiers');
            if (!input) return;

            function taille(o) {
                return o < 1024 * 1024 ? Math.max(1, Math.round(o / 1024)) + ' Ko' : (o / 1024 / 1024).toFixed(1) + ' Mo';
            }
            input.addEventListener('change', function () {
                liste.innerHTML = '';
                Array.prototype.forEach.call(input.files, function (f) {
                    var li = document.createElement('li');
                    var nom = document.createElement('span');
                    var poids = document.createElement('span');
                    nom.textContent = f.name;            // textContent : pas d'injection HTML
                    poids.textContent = taille(f.size);
                    if (f.size > 5 * 1024 * 1024) { poids.textContent += ' – trop volumineux'; poids.style.color = '#B3261E'; }
                    li.appendChild(nom);
                    li.appendChild(poids);
                    liste.appendChild(li);
                });
            });
            ['dragenter', 'dragover'].forEach(function (e) { depot.addEventListener(e, function () { depot.classList.add('gp-survol'); }); });
            ['dragleave', 'drop'].forEach(function (e) { depot.addEventListener(e, function () { depot.classList.remove('gp-survol'); }); });
        })();
    </script>
</body>
</html>
