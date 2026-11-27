<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- Après une erreur, on réaffiche ce qui a été saisi plutôt que les valeurs en base --%>
<c:set var="saisie" value="${not empty erreurs}"/>
<c:set var="vNom" value="${saisie ? param.nom : utilisateur.nom}"/>
<c:set var="vPrenom" value="${saisie ? param.prenom : utilisateur.prenom}"/>
<c:set var="vEmail" value="${saisie ? param.email : utilisateur.email}"/>
<c:set var="vRole" value="${saisie ? param.role : (empty utilisateur ? 'CLIENT' : utilisateur.role)}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>${empty utilisateur ? 'Nouvel utilisateur' : 'Modifier l\'utilisateur'} – Assurance Gros Pépin</title>
    <%@ include file="/WEB-INF/views/commun/head.jspf" %>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="gp-page">
        <section class="gp-bandeau">
            <div class="gp-conteneur">
                <ol class="gp-fil">
                    <li><a href="${baseUrl}">Utilisateurs</a></li>
                    <li>${empty utilisateur ? 'Nouvel utilisateur' : 'Modifier'}</li>
                </ol>
                <div class="gp-bandeau-ligne">
                    <div>
                        <h1 class="gp-titre">${empty utilisateur ? 'Nouvel utilisateur' : 'Modifier l\'utilisateur'}</h1>
                        <p class="gp-sous-titre">${empty utilisateur ? 'Créez un compte et attribuez-lui son rôle dans l\'application.' : 'Modifiez les informations, le rôle ou le mot de passe du compte.'}</p>
                    </div>
                    <c:if test="${not empty utilisateur}">
                        <span class="gp-avatar">${WebUtil.initiales(utilisateur.prenom, utilisateur.nom)}</span>
                    </c:if>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <c:if test="${saisie}">
                <div class="gp-message gp-message-erreur">
                    <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 7.5v5.5M12 16.5h.01"/></svg>
                    <span><strong>Le compte n'a pas été enregistré.</strong> Corrigez les champs signalés ci-dessous.</span>
                </div>
            </c:if>

            <form action="${baseUrl}" method="post" class="gp-carte">
                <input type="hidden" name="action" value="save">
                <c:if test="${not empty utilisateur}"><input type="hidden" name="id" value="${utilisateur.idUtilisateur}"></c:if>

                <div class="gp-form-section">
                    <h2 class="gp-etape-titre"><span class="gp-etape-num">1</span> Identité et e-mail</h2>
                    <p class="gp-etape-aide">Le nom, le prénom et l'adresse e-mail de connexion. L'e-mail doit être unique.</p>
                    <div class="gp-form-grille">
                        <div class="gp-champ ${not empty erreurs.prenom ? 'gp-champ-invalide' : ''}">
                            <label for="prenom">Prénom</label>
                            <input type="text" id="prenom" name="prenom" maxlength="50" required value="<c:out value='${vPrenom}'/>">
                            <c:if test="${not empty erreurs.prenom}"><p class="gp-erreur"><c:out value="${erreurs.prenom}"/></p></c:if>
                        </div>
                        <div class="gp-champ ${not empty erreurs.nom ? 'gp-champ-invalide' : ''}">
                            <label for="nom">Nom</label>
                            <input type="text" id="nom" name="nom" maxlength="50" required value="<c:out value='${vNom}'/>">
                            <c:if test="${not empty erreurs.nom}"><p class="gp-erreur"><c:out value="${erreurs.nom}"/></p></c:if>
                        </div>
                        <div class="gp-champ ${not empty erreurs.email ? 'gp-champ-invalide' : ''}">
                            <label for="email">E-mail</label>
                            <input type="email" id="email" name="email" maxlength="100" required placeholder="jean.dupont@exemple.fr" value="<c:out value='${vEmail}'/>">
                            <c:if test="${not empty erreurs.email}"><p class="gp-erreur"><c:out value="${erreurs.email}"/></p></c:if>
                        </div>
                    </div>
                </div>

                <div class="gp-form-section">
                    <h2 class="gp-etape-titre"><span class="gp-etape-num">2</span> Rôle et permissions</h2>
                    <p class="gp-etape-aide">Le rôle détermine l'espace accessible : client (son espace personnel), agent (gestion des contrats et sinistres), administrateur (tout, y compris cette page).</p>
                    <div class="gp-form-grille">
                        <div class="gp-champ ${not empty erreurs.role ? 'gp-champ-invalide' : ''}">
                            <label for="role">Rôle</label>
                            <select id="role" name="role" required>
                                <c:forEach var="r" items="${roles}">
                                    <option value="${r}" ${vRole == r ? 'selected' : ''}>${r}</option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty erreurs.role}"><p class="gp-erreur"><c:out value="${erreurs.role}"/></p></c:if>
                        </div>
                    </div>
                </div>

                <div class="gp-form-section">
                    <h2 class="gp-etape-titre"><span class="gp-etape-num">3</span> Mot de passe</h2>
                    <c:choose>
                        <c:when test="${empty utilisateur}">
                            <p class="gp-etape-aide">Mot de passe initial du compte : 6 caractères minimum. Il est stocké chiffré (hash).</p>
                        </c:when>
                        <c:otherwise>
                            <p class="gp-etape-aide">Laissez ce champ vide pour conserver le mot de passe actuel. Renseignez-le pour le réinitialiser.</p>
                        </c:otherwise>
                    </c:choose>
                    <div class="gp-form-grille">
                        <div class="gp-champ ${not empty erreurs.motDePasse ? 'gp-champ-invalide' : ''}">
                            <label for="motDePasse">${empty utilisateur ? 'Mot de passe' : 'Nouveau mot de passe (optionnel)'}</label>
                            <input type="password" id="motDePasse" name="motDePasse" maxlength="100"
                                   autocomplete="new-password" ${empty utilisateur ? 'required' : ''}>
                            <c:if test="${not empty erreurs.motDePasse}"><p class="gp-erreur"><c:out value="${erreurs.motDePasse}"/></p></c:if>
                        </div>
                    </div>
                </div>

                <div class="gp-form-actions">
                    <a href="${baseUrl}" class="gp-bouton-contour">Annuler</a>
                    <button type="submit" class="gp-bouton">
                        <svg viewBox="0 0 24 24"><path d="M5 12.5 10 17.5 19 7"/></svg>
                        ${empty utilisateur ? 'Créer le compte' : 'Enregistrer'}
                    </button>
                </div>
            </form>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
