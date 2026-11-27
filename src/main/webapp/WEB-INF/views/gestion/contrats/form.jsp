<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- Après une erreur, on réaffiche ce qui a été saisi plutôt que les valeurs en base --%>
<c:set var="saisie" value="${not empty erreurs}"/>
<c:set var="vNum" value="${saisie ? param.numContrat : (empty contrat ? numeroPropose : contrat.numContrat)}"/>
<%-- ?client=.. depuis la fiche client : le client est déjà choisi --%>
<c:set var="vClient" value="${saisie ? param.idUtilisateur : (empty contrat ? param.client : contrat.utilisateur.idUtilisateur)}"/>
<c:set var="vType" value="${saisie ? param.idTypeAssurance : contrat.typeAssurance.idTypeAssurance}"/>
<c:set var="vPrime" value="${saisie ? param.prime : contrat.prime}"/>
<c:set var="vDebut" value="${saisie ? param.dateDebut : (empty contrat ? debutPropose : contrat.dateDebut)}"/>
<c:set var="vFin" value="${saisie ? param.dateFin : (empty contrat ? finProposee : contrat.dateFin)}"/>
<%-- Un contrat créé par un agent est actif par défaut (il peut choisir "En attente") --%>
<c:set var="vStatut" value="${saisie ? param.statut : (empty contrat ? 'ACTIF' : contrat.statut.toString())}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>${empty contrat ? 'Nouveau contrat' : 'Modifier le contrat'} – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <ol class="gp-fil">
                    <li><a href="${baseUrl}">Contrats</a></li>
                    <c:if test="${not empty contrat}"><li><a href="${baseUrl}?action=detail&id=${contrat.idContrat}"><c:out value="${contrat.numContrat}"/></a></li></c:if>
                    <li>${empty contrat ? 'Nouveau contrat' : 'Modifier'}</li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div>
                        <h1 class="gp-titre">${empty contrat ? 'Nouveau contrat' : 'Modifier le contrat'}</h1>
                        <p class="gp-sous-titre">Renseignez le titulaire, le produit souscrit, la période et les garanties incluses.</p>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <c:if test="${saisie}">
                <div class="gp-message gp-message-erreur">
                    <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 7.5v5.5M12 16.5h.01"/></svg>
                    <span><strong>Le contrat n'a pas été enregistré.</strong> Corrigez les champs signalés ci-dessous.</span>
                </div>
            </c:if>

            <form action="${baseUrl}" method="post" class="gp-carte">
                <input type="hidden" name="action" value="save">
                <c:if test="${not empty contrat}"><input type="hidden" name="id" value="${contrat.idContrat}"></c:if>

                <div class="gp-form-section">
                    <h2 class="gp-etape-titre"><span class="gp-etape-num">1</span> Titulaire et produit</h2>
                    <p class="gp-etape-aide">Le client couvert et le type d'assurance souscrit. Choisir le produit remplit la prime et coche ses garanties.</p>
                    <div class="gp-form-grille">
                        <div class="gp-champ ${not empty erreurs.idUtilisateur ? 'gp-champ-invalide' : ''}">
                            <label for="idUtilisateur">Client</label>
                            <select id="idUtilisateur" name="idUtilisateur" required>
                                <option value="">Choisissez un client</option>
                                <c:forEach var="cl" items="${clients}">
                                    <option value="${cl.idUtilisateur}" ${vClient eq ''.concat(cl.idUtilisateur) ? 'selected' : ''}>
                                        <c:out value="${cl.prenom} ${cl.nom} – ${cl.email}"/>
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty erreurs.idUtilisateur}"><p class="gp-erreur"><c:out value="${erreurs.idUtilisateur}"/></p></c:if>
                        </div>
                        <div class="gp-champ ${not empty erreurs.idTypeAssurance ? 'gp-champ-invalide' : ''}">
                            <label for="idTypeAssurance">Type d'assurance</label>
                            <select id="idTypeAssurance" name="idTypeAssurance" required>
                                <option value="">Choisissez un produit</option>
                                <c:forEach var="t" items="${typesAssurance}">
                                    <c:set var="idsGaranties" value=""/>
                                    <c:forEach var="gd" items="${t.garantiesDisponibles}"><c:set var="idsGaranties" value="${idsGaranties},${gd.idGarantie}"/></c:forEach>
                                    <option value="${t.idTypeAssurance}" data-prix="${t.prixBase}" data-garanties="${idsGaranties}," ${vType eq t.idTypeAssurance.toString() ? 'selected' : ''}>
                                        <c:out value="${t.nom}"/>
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty erreurs.idTypeAssurance}"><p class="gp-erreur"><c:out value="${erreurs.idTypeAssurance}"/></p></c:if>
                        </div>
                        <div class="gp-champ ${not empty erreurs.numContrat ? 'gp-champ-invalide' : ''}">
                            <label for="numContrat">Numéro de contrat</label>
                            <input type="text" id="numContrat" name="numContrat" maxlength="50" required
                                   placeholder="CTR-2026-003" value="<c:out value='${vNum}'/>">
                            <c:if test="${not empty erreurs.numContrat}"><p class="gp-erreur"><c:out value="${erreurs.numContrat}"/></p></c:if>
                        </div>
                        <div class="gp-champ">
                            <label for="statut">Statut</label>
                            <select id="statut" name="statut">
                                <c:forEach var="st" items="${statuts}">
                                    <option value="${st}" ${vStatut eq st.toString() ? 'selected' : ''}>${st.libelle}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>

                <div class="gp-form-section">
                    <h2 class="gp-etape-titre"><span class="gp-etape-num">2</span> Période et cotisation</h2>
                    <p class="gp-etape-aide">Dates de couverture et prime annuelle.</p>
                    <div class="gp-form-grille">
                        <div class="gp-champ ${not empty erreurs.dateDebut ? 'gp-champ-invalide' : ''}">
                            <label for="dateDebut">Date d'effet</label>
                            <input type="date" id="dateDebut" name="dateDebut" required value="<c:out value='${vDebut}'/>">
                            <c:if test="${not empty erreurs.dateDebut}"><p class="gp-erreur"><c:out value="${erreurs.dateDebut}"/></p></c:if>
                        </div>
                        <div class="gp-champ ${not empty erreurs.dateFin ? 'gp-champ-invalide' : ''}">
                            <label for="dateFin">Date d'échéance</label>
                            <input type="date" id="dateFin" name="dateFin" required value="<c:out value='${vFin}'/>">
                            <c:if test="${not empty erreurs.dateFin}"><p class="gp-erreur"><c:out value="${erreurs.dateFin}"/></p></c:if>
                        </div>
                        <div class="gp-champ ${not empty erreurs.prime ? 'gp-champ-invalide' : ''}">
                            <label for="prime">Prime annuelle</label>
                            <div class="gp-champ-montant">
                                <input type="number" id="prime" name="prime" step="0.01" min="0.01" required value="<c:out value='${vPrime}'/>">
                            </div>
                            <c:if test="${not empty erreurs.prime}"><p class="gp-erreur"><c:out value="${erreurs.prime}"/></p></c:if>
                        </div>
                        <c:if test="${empty contrat}">
                            <div class="gp-champ">
                                <label for="echeances">Échéancier de paiement</label>
                                <select id="echeances" name="echeances">
                                    <option value="12" ${param.echeances == '12' or empty param.echeances ? 'selected' : ''}>Mensuel (12 prélèvements)</option>
                                    <option value="4" ${param.echeances == '4' ? 'selected' : ''}>Trimestriel (4 prélèvements)</option>
                                    <option value="1" ${param.echeances == '1' ? 'selected' : ''}>Annuel (1 prélèvement)</option>
                                </select>
                                <span class="gp-aide">Créé automatiquement si le contrat est actif.</span>
                            </div>
                        </c:if>
                    </div>
                </div>

                <div class="gp-form-section">
                    <h2 class="gp-etape-titre"><span class="gp-etape-num">3</span> Garanties incluses</h2>
                    <p class="gp-etape-aide">Cochez les garanties couvertes par ce contrat.</p>
                    <fieldset class="gp-choix gp-choix-large">
                        <legend class="gp-visuellement-cache">Garanties</legend>
                        <c:forEach var="g" items="${toutesLesGaranties}">
                            <c:set var="coche" value="false"/>
                            <c:choose>
                                <c:when test="${saisie}">
                                    <c:forEach var="gid" items="${paramValues.garanties}">
                                        <c:if test="${gid eq g.idGarantie.toString()}"><c:set var="coche" value="true"/></c:if>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="gc" items="${contrat.garanties}">
                                        <c:if test="${gc.idGarantie == g.idGarantie}"><c:set var="coche" value="true"/></c:if>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            <label class="gp-choix-option">
                                <input type="checkbox" name="garanties" value="${g.idGarantie}" ${coche ? 'checked' : ''}>
                                <span class="gp-choix-carte">
                                    <span class="gp-choix-texte">
                                        <strong><c:out value="${g.nom}"/></strong>
                                        <small><c:out value="${g.description}"/></small>
                                    </span>
                                    <span class="gp-choix-coche"></span>
                                </span>
                            </label>
                        </c:forEach>
                    </fieldset>
                </div>

                <div class="gp-form-actions">
                    <a href="${empty contrat ? baseUrl : baseUrl.concat('?action=detail&id=').concat(contrat.idContrat)}" class="gp-bouton-contour">Annuler</a>
                    <button type="submit" class="gp-bouton">${empty contrat ? 'Créer le contrat' : 'Enregistrer les modifications'}</button>
                </div>
            </form>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>

    <script>
        // Choisir un produit : prime de base + garanties du produit pré-cochées (tout reste modifiable)
        (function () {
            var type = document.getElementById('idTypeAssurance');
            var prime = document.getElementById('prime');
            var debut = document.getElementById('dateDebut');
            var fin = document.getElementById('dateFin');
            if (!type) return;

            type.addEventListener('change', function () {
                var opt = type.options[type.selectedIndex];
                if (!opt || !opt.dataset.prix) return;
                prime.value = opt.dataset.prix;
                var ids = opt.dataset.garanties || ',';
                document.querySelectorAll('input[name="garanties"]').forEach(function (cb) {
                    cb.checked = ids.indexOf(',' + cb.value + ',') !== -1;
                });
            });

            // Changer la date d'effet décale l'échéance d'un an
            debut.addEventListener('change', function () {
                if (!debut.value) return;
                var d = new Date(debut.value + 'T00:00:00');
                d.setFullYear(d.getFullYear() + 1);
                d.setDate(d.getDate() - 1);
                fin.value = d.toISOString().slice(0, 10);
            });
        })();
    </script>
</body>
</html>
