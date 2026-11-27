<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- Après une erreur sur le formulaire "infos", on réaffiche ce que l'utilisateur a saisi --%>
<c:set var="erreurInfos" value="${param.action == 'infos' and not empty erreurs}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Mon profil – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <ol class="gp-fil">
                    <li><a href="${pageContext.request.contextPath}/client/dashboard">Mon espace</a></li>
                    <li>Mon profil</li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div>
                        <h1 class="gp-titre">Mon profil</h1>
                        <p class="gp-sous-titre">Gérez vos informations personnelles et la sécurité de votre compte.</p>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <%@ include file="/WEB-INF/views/commun/flash.jspf" %>

            <div class="gp-colonnes gp-colonnes-gauche">
                <aside class="gp-pile gp-collant">
                    <section class="gp-carte gp-carte-corps gp-profil-carte">
                        <span class="gp-avatar">${WebUtil.initiales(profil.prenom, profil.nom)}</span>
                        <p class="gp-profil-nom"><c:out value="${profil.prenom} ${profil.nom}"/></p>
                        <p class="gp-profil-email"><c:out value="${profil.email}"/></p>
                        <hr class="gp-separateur">
                        <dl class="gp-donnees" style="text-align:left">
                            <div><dt>N° client</dt><dd>${WebUtil.numeroClient(profil.idUtilisateur)}</dd></div>
                            <div><dt>Type de compte</dt><dd>Sociétaire</dd></div>
                        </dl>
                    </section>
                    <a href="${pageContext.request.contextPath}/logout" class="gp-bouton-contour gp-bouton-bloc">
                        <svg viewBox="0 0 24 24"><path d="M15 4h4v16h-4M10 8l-4 4 4 4M6 12h10"/></svg>
                        Se déconnecter
                    </a>
                </aside>

                <div class="gp-pile">
                    <%-- ===== Informations personnelles ===== --%>
                    <form method="post" action="${pageContext.request.contextPath}/client/profil" class="gp-carte">
                        <input type="hidden" name="action" value="infos">
                        <div class="gp-form-section">
                            <h2 class="gp-etape-titre">
                                <span class="gp-icone" style="width:36px;height:36px"><svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 3.6-6.5 8-6.5s8 2.5 8 6.5"/></svg></span>
                                Informations personnelles
                            </h2>
                            <p class="gp-etape-aide" style="margin-left:48px">Ces informations sont utilisées sur vos contrats et pour vous contacter.</p>

                            <div class="gp-form-grille">
                                <div class="gp-champ ${not empty erreurs.prenom ? 'gp-champ-invalide' : ''}">
                                    <label for="prenom">Prénom</label>
                                    <input type="text" id="prenom" name="prenom" maxlength="50" required autocomplete="given-name"
                                           value="<c:out value='${erreurInfos ? param.prenom : profil.prenom}'/>">
                                    <c:if test="${not empty erreurs.prenom}"><p class="gp-erreur"><c:out value="${erreurs.prenom}"/></p></c:if>
                                </div>
                                <div class="gp-champ ${not empty erreurs.nom ? 'gp-champ-invalide' : ''}">
                                    <label for="nom">Nom</label>
                                    <input type="text" id="nom" name="nom" maxlength="50" required autocomplete="family-name"
                                           value="<c:out value='${erreurInfos ? param.nom : profil.nom}'/>">
                                    <c:if test="${not empty erreurs.nom}"><p class="gp-erreur"><c:out value="${erreurs.nom}"/></p></c:if>
                                </div>
                            </div>
                            <div class="gp-champ ${not empty erreurs.email ? 'gp-champ-invalide' : ''}">
                                <label for="email">Adresse e-mail</label>
                                <input type="email" id="email" name="email" maxlength="100" required autocomplete="email"
                                       value="<c:out value='${erreurInfos ? param.email : profil.email}'/>">
                                <span class="gp-aide">Elle vous sert d'identifiant de connexion.</span>
                                <c:if test="${not empty erreurs.email}"><p class="gp-erreur"><c:out value="${erreurs.email}"/></p></c:if>
                            </div>
                        </div>
                        <div class="gp-form-actions">
                            <span></span>
                            <button type="submit" class="gp-bouton">Enregistrer</button>
                        </div>
                    </form>

                    <%-- ===== Mot de passe ===== --%>
                    <form method="post" action="${pageContext.request.contextPath}/client/profil" class="gp-carte" id="securite">
                        <input type="hidden" name="action" value="motDePasse">
                        <div class="gp-form-section">
                            <h2 class="gp-etape-titre">
                                <span class="gp-icone" style="width:36px;height:36px"><svg viewBox="0 0 24 24"><path d="M5.5 11h13v10h-13zM8.5 11V8a3.5 3.5 0 0 1 7 0v3M12 15v2.5"/></svg></span>
                                Mot de passe
                            </h2>
                            <p class="gp-etape-aide" style="margin-left:48px">Pour votre sécurité, saisissez d'abord votre mot de passe actuel.</p>

                            <div class="gp-champ ${not empty erreurs.motDePasseActuel ? 'gp-champ-invalide' : ''}" style="max-width:420px">
                                <label for="motDePasseActuel">Mot de passe actuel</label>
                                <div class="gp-mdp">
                                    <input type="password" id="motDePasseActuel" name="motDePasseActuel" required autocomplete="current-password">
                                    <button type="button" data-afficher="motDePasseActuel">Afficher</button>
                                </div>
                                <c:if test="${not empty erreurs.motDePasseActuel}"><p class="gp-erreur"><c:out value="${erreurs.motDePasseActuel}"/></p></c:if>
                            </div>
                            <div class="gp-form-grille">
                                <div class="gp-champ ${not empty erreurs.nouveauMotDePasse ? 'gp-champ-invalide' : ''}">
                                    <label for="nouveauMotDePasse">Nouveau mot de passe</label>
                                    <div class="gp-mdp">
                                        <input type="password" id="nouveauMotDePasse" name="nouveauMotDePasse" minlength="6" required autocomplete="new-password">
                                        <button type="button" data-afficher="nouveauMotDePasse">Afficher</button>
                                    </div>
                                    <span class="gp-aide">6 caractères minimum.</span>
                                    <c:if test="${not empty erreurs.nouveauMotDePasse}"><p class="gp-erreur"><c:out value="${erreurs.nouveauMotDePasse}"/></p></c:if>
                                </div>
                                <div class="gp-champ ${not empty erreurs.confirmation ? 'gp-champ-invalide' : ''}">
                                    <label for="confirmation">Confirmer le mot de passe</label>
                                    <div class="gp-mdp">
                                        <input type="password" id="confirmation" name="confirmation" minlength="6" required autocomplete="new-password">
                                        <button type="button" data-afficher="confirmation">Afficher</button>
                                    </div>
                                    <c:if test="${not empty erreurs.confirmation}"><p class="gp-erreur"><c:out value="${erreurs.confirmation}"/></p></c:if>
                                </div>
                            </div>
                        </div>
                        <div class="gp-form-actions">
                            <span></span>
                            <button type="submit" class="gp-bouton">Modifier le mot de passe</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>

    <script>
        // Boutons "Afficher / Masquer" des champs mot de passe
        document.querySelectorAll('[data-afficher]').forEach(function (bouton) {
            bouton.addEventListener('click', function () {
                var champ = document.getElementById(bouton.dataset.afficher);
                var visible = champ.type === 'text';
                champ.type = visible ? 'password' : 'text';
                bouton.textContent = visible ? 'Afficher' : 'Masquer';
            });
        });
        <c:if test="${ongletMotDePasse}">document.getElementById('securite').scrollIntoView();</c:if>
    </script>
</body>
</html>
