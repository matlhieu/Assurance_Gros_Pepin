<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fr.grouped.projet_assurance.util.WebUtil" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Garanties – Assurance Gros Pépin</title>
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
                        <h1 class="gp-titre">Garanties</h1>
                        <p class="gp-sous-titre">Les ${garanties.size()} garanties proposées, et les produits qui les incluent.</p>
                    </div>
                    <div class="gp-bandeau-actions">
                        <a href="#formulaire" class="gp-bouton">
                            <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                            Nouvelle garantie
                        </a>
                    </div>
                </div>
            </div>
        </section>

        <div class="gp-conteneur gp-contenu">
            <%@ include file="/WEB-INF/views/commun/flash.jspf" %>

            <%-- ===== Par produit ===== --%>
            <div class="gp-grille gp-grille-2">
                <c:forEach var="t" items="${types}">
                    <section class="gp-carte">
                        <div class="gp-carte-entete">
                            <div style="display:flex;align-items:center;gap:14px">
                                <c:set var="nomType" value="${t.nom}"/>
                                <span class="gp-icone"><%@ include file="/WEB-INF/views/commun/icone-assurance.jspf" %></span>
                                <div>
                                    <h2 class="gp-carte-titre"><c:out value="${t.nom}"/></h2>
                                    <span class="gp-discret">À partir de ${WebUtil.euros(t.prixBase)} / an</span>
                                </div>
                            </div>
                            <span class="gp-discret">${t.garantiesDisponibles.size()} garantie(s)</span>
                        </div>
                        <div class="gp-carte-corps">
                            <c:if test="${not empty t.description}"><p class="gp-discret" style="margin:0 0 14px"><c:out value="${t.description}"/></p></c:if>
                            <c:choose>
                                <c:when test="${empty t.garantiesDisponibles}">
                                    <p class="gp-discret" style="margin:0">Aucune garantie n'est encore associée à ce produit.</p>
                                </c:when>
                                <c:otherwise>
                                    <ul class="gp-lignes gp-lignes-compactes">
                                        <c:forEach var="g" items="${t.garantiesDisponibles}">
                                            <li><span class="gp-puce-ok"></span><c:out value="${g.nom}"/></li>
                                        </c:forEach>
                                    </ul>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </section>
                </c:forEach>
            </div>

            <%-- ===== Toutes les garanties ===== --%>
            <div class="gp-colonnes gp-section">
                <section>
                    <h2 class="gp-section-titre">Toutes les garanties</h2>
                    <div class="gp-carte">
                        <div class="gp-tableau-boite">
                            <table class="gp-tableau">
                                <thead>
                                    <tr>
                                        <th>Garantie</th>
                                        <th class="gp-num">Contrats actifs</th>
                                        <th class="gp-actions"><span class="gp-visuellement-cache">Actions</span></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="g" items="${garanties}">
                                        <tr ${garantieModifiee.idGarantie == g.idGarantie ? 'style="background:#FCF7FC"' : ''}>
                                            <td><strong><c:out value="${g.nom}"/></strong>
                                                <small><c:out value="${g.description}"/></small></td>
                                            <td class="gp-num">${empty utilisation[g.idGarantie] ? 0 : utilisation[g.idGarantie]}</td>
                                            <td class="gp-actions">
                                                <a href="?modifier=${g.idGarantie}#formulaire" class="gp-lien">Modifier</a>
                                                <c:choose>
                                                    <c:when test="${empty usages[g.idGarantie]}">
                                                        <form method="post" action="${pageContext.request.contextPath}/agent/garanties" style="display:inline;margin-left:16px">
                                                            <input type="hidden" name="action" value="supprimer">
                                                            <input type="hidden" name="id" value="${g.idGarantie}">
                                                            <button type="submit" class="gp-lien gp-lien-danger" style="background:none;border:none;padding:0;font:inherit;font-weight:700;cursor:pointer"
                                                                    onclick="return confirm('Supprimer cette garantie ?');">Supprimer</button>
                                                        </form>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="gp-discret" style="margin-left:16px" title="Utilisée par ${usages[g.idGarantie]} contrat(s) ou produit(s)">Utilisée</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </section>

                <aside class="gp-collant" id="formulaire">
                    <h2 class="gp-section-titre">${empty garantieModifiee ? 'Nouvelle garantie' : 'Modifier la garantie'}</h2>
                    <form method="post" action="${pageContext.request.contextPath}/agent/garanties" class="gp-carte">
                        <input type="hidden" name="action" value="enregistrer">
                        <c:if test="${not empty garantieModifiee}"><input type="hidden" name="id" value="${garantieModifiee.idGarantie}"></c:if>
                        <div class="gp-form-section">
                            <div class="gp-champ ${not empty erreurs.nom ? 'gp-champ-invalide' : ''}">
                                <label for="nom">Nom</label>
                                <input type="text" id="nom" name="nom" maxlength="50" required placeholder="Ex. : Bris de glace"
                                       value="<c:out value='${not empty erreurs ? param.nom : garantieModifiee.nom}'/>">
                                <c:if test="${not empty erreurs.nom}"><p class="gp-erreur"><c:out value="${erreurs.nom}"/></p></c:if>
                            </div>
                            <div class="gp-champ ${not empty erreurs.description ? 'gp-champ-invalide' : ''}">
                                <label for="description">Description <span class="gp-optionnel">(visible par les clients)</span></label>
                                <textarea id="description" name="description" maxlength="500" style="min-height:100px"
                                          placeholder="Ce que couvre la garantie"><c:out value="${not empty erreurs ? param.description : garantieModifiee.description}"/></textarea>
                                <c:if test="${not empty erreurs.description}"><p class="gp-erreur"><c:out value="${erreurs.description}"/></p></c:if>
                            </div>
                        </div>
                        <div class="gp-form-actions">
                            <c:choose>
                                <c:when test="${not empty garantieModifiee}"><a href="${pageContext.request.contextPath}/agent/garanties" class="gp-lien">Annuler</a></c:when>
                                <c:otherwise><span></span></c:otherwise>
                            </c:choose>
                            <button type="submit" class="gp-bouton">${empty garantieModifiee ? 'Créer' : 'Enregistrer'}</button>
                        </div>
                    </form>
                </aside>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
