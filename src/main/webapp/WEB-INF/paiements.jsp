<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title><c:choose><c:when test="${isAgent}">Espace Agent - Paiements</c:when><c:otherwise>Espace Client - Mes Paiements</c:otherwise></c:choose></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header class="entete">
    <a href="${pageContext.request.contextPath}/index.jsp">
        <img src="${pageContext.request.contextPath}/image/logo.png" alt="Logo Assurance" class="logo">
    </a>
</header>

<div class="conteneur">

    <!-- En-tête de la page -->
    <div class="titre-page">
        <h2>
            <c:choose>
                <c:when test="${isAgent}">Espace Agent - Gestion des Paiements</c:when>
                <c:otherwise>Espace Client - Mes Paiements</c:otherwise>
            </c:choose>
        </h2>
        <div class="nav-liens">
            <a href="${pageContext.request.contextPath}/client/paiements" class="${!isAgent ? 'actif' : ''}">Vue Client</a>
            <a href="${pageContext.request.contextPath}/agent/paiements" class="${isAgent ? 'actif' : ''}">Vue Agent</a>
        </div>
    </div>

    <!-- Messages -->
    <c:if test="${not empty messageSucces}">
        <div class="alerte alerte-succes">
            <strong>Succès :</strong> ${messageSucces}
        </div>
    </c:if>
    <c:if test="${not empty messageErreur}">
        <div class="alerte alerte-erreur">
            <strong>Erreur :</strong> ${messageErreur}
        </div>
    </c:if>

    <!-- Résumé sobre -->
    <div class="resume-barre">
        <div class="resume-item">Total réglé : <span>${totalPaye != null ? totalPaye : 0.00} €</span></div>
        <div class="resume-item">Échéances en attente : <span>${totalEnAttente != null ? totalEnAttente : 0.00} €</span></div>
        <div class="resume-item">Total opérations : <span>${paiements != null ? paiements.size() : 0}</span></div>
    </div>

    <!-- Formulaire d'encaissement réservé à l'agent -->
    <c:if test="${isAgent}">
        <fieldset class="cadre-formulaire">
            <legend>Enregistrer un encaissement</legend>
            <form action="${pageContext.request.contextPath}/agent/paiements" method="POST">
                <input type="hidden" name="action" value="creer">
                <div class="form-ligne">
                    <div class="form-groupe">
                        <label for="idContrat">N° Contrat :</label>
                        <input type="number" id="idContrat" name="idContrat" required min="1" placeholder="Ex: 1" value="${saisieIdContrat}" style="width: 90px;">
                    </div>
                    <div class="form-groupe">
                        <label for="montant">Montant (€) :</label>
                        <input type="number" id="montant" name="montant" required min="0.01" step="0.01" placeholder="Ex: 150.00" value="${saisieMontant}" style="width: 110px;">
                    </div>
                    <div class="form-groupe">
                        <label for="datePaiement">Date :</label>
                        <input type="date" id="datePaiement" name="datePaiement" value="${saisieDatePaiement}">
                    </div>
                    <div class="form-groupe">
                        <label for="modePaiement">Mode :</label>
                        <select id="modePaiement" name="modePaiement" required>
                            <option value="CARTE" ${saisieModePaiement == 'CARTE' ? 'selected' : ''}>Carte bancaire</option>
                            <option value="VIREMENT" ${saisieModePaiement == 'VIREMENT' ? 'selected' : ''}>Virement</option>
                            <option value="PRELEVEMENT" ${saisieModePaiement == 'PRELEVEMENT' ? 'selected' : ''}>Prélèvement</option>
                            <option value="ESPECES" ${saisieModePaiement == 'ESPECES' ? 'selected' : ''}>Espèces</option>
                            <option value="CHEQUE" ${saisieModePaiement == 'CHEQUE' ? 'selected' : ''}>Chèque</option>
                        </select>
                    </div>
                    <div class="form-groupe">
                        <label for="statut">Statut :</label>
                        <select id="statut" name="statut">
                            <option value="EFFECTUE" ${saisieStatut == 'EFFECTUE' ? 'selected' : ''}>EFFECTUE</option>
                            <option value="EN_ATTENTE" ${saisieStatut == 'EN_ATTENTE' ? 'selected' : ''}>EN_ATTENTE</option>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-submit">Enregistrer</button>
                </div>
            </form>
        </fieldset>
    </c:if>

    <!-- Zone de filtre -->
    <div class="zone-filtre">
        <form method="GET" action="${isAgent ? pageContext.request.contextPath.concat('/agent/paiements') : pageContext.request.contextPath.concat('/client/paiements')}">
            <label for="filtreContrat">Filtrer par n° de contrat :</label>
            <input type="number" id="filtreContrat" name="idContrat" placeholder="ID" value="${filtreContrat}">
            <button type="submit" class="btn">Filtrer</button>
            <c:if test="${not empty filtreContrat}">
                <a href="${isAgent ? pageContext.request.contextPath.concat('/agent/paiements') : pageContext.request.contextPath.concat('/client/paiements')}" style="color: #666; font-size: 12px; margin-left: 8px;">Réinitialiser</a>
            </c:if>
        </form>
    </div>

    <!-- Section 1 : Échéances en attente -->
    <h3 class="sous-titre">Échéances en attente</h3>
    <c:choose>
        <c:when test="${not empty paiementsEnAttente}">
            <table class="table-sobre">
                <thead>
                <tr>
                    <th>N° Échéance</th>
                    <th>N° Contrat</th>
                    <th>Date d'échéance</th>
                    <th>Mode prévu</th>
                    <th>Montant</th>
                    <th>Statut</th>
                    <c:if test="${isAgent}">
                        <th>Action</th>
                    </c:if>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="p" items="${paiementsEnAttente}">
                    <tr>
                        <td>#${p.idPaiement}</td>
                        <td>Contrat N°${p.idContrat}</td>
                        <td>${p.datePaiement}</td>
                        <td>${p.modePaiement}</td>
                        <td>${p.montant} €</td>
                        <td><span class="statut-attente">${p.statut}</span></td>
                        <c:if test="${isAgent}">
                            <td>
                                <form action="${pageContext.request.contextPath}/agent/paiements" method="POST" style="margin: 0; display: inline;">
                                    <input type="hidden" name="action" value="encaisser">
                                    <input type="hidden" name="idPaiement" value="${p.idPaiement}">
                                    <button type="submit" class="btn btn-action">Encaisser</button>
                                </form>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div class="aucun-enregistrement">Aucune échéance en attente de paiement.</div>
        </c:otherwise>
    </c:choose>

    <!-- Section 2 : Historique des paiements effectués -->
    <h3 class="sous-titre">Historique des paiements effectués</h3>
    <c:choose>
        <c:when test="${not empty paiementsEffectues}">
            <table class="table-sobre">
                <thead>
                <tr>
                    <th>N° Paiement</th>
                    <th>N° Contrat</th>
                    <th>Date</th>
                    <th>Mode de règlement</th>
                    <th>Montant</th>
                    <th>Statut</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="p" items="${paiementsEffectues}">
                    <tr>
                        <td>#${p.idPaiement}</td>
                        <td>Contrat N°${p.idContrat}</td>
                        <td>${p.datePaiement}</td>
                        <td>${p.modePaiement}</td>
                        <td>${p.montant} €</td>
                        <td><span class="statut-effectue">${p.statut}</span></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div class="aucun-enregistrement">Aucun paiement effectué pour l'instant.</div>
        </c:otherwise>
    </c:choose>

</div>

<%@ include file="/WEB-INF/header.jspf" %>

</body>
</html>
