<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gestion des Paiements – Espace Agent</title>
    <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png">
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .page-container {
            max-width: 1100px;
            margin: 40px auto;
            padding: 0 24px;
        }
        .page-header {
            margin-bottom: 28px;
        }
        .page-title {
            font-size: 2rem;
            font-weight: 800;
            margin: 0 0 8px;
            color: var(--texte);
        }
        .page-subtitle {
            color: var(--texte-doux);
            font-size: 1.05rem;
            margin: 0;
        }
        .form-card {
            background: #fff;
            border: 1px solid var(--bordure);
            border-radius: 8px;
            padding: 24px;
            margin-bottom: 32px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.04);
        }
        .form-card h2 {
            margin: 0 0 20px;
            font-size: 1.25rem;
            color: var(--orchidee);
        }
        .form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 16px;
            align-items: flex-end;
        }
        .form-group {
            display: flex;
            flex-direction: column;
        }
        .form-group label {
            font-weight: 700;
            font-size: 0.9rem;
            margin-bottom: 6px;
            color: var(--texte);
        }
        .form-group input,
        .form-group select {
            height: 44px;
            padding: 0 12px;
            border: 1px solid var(--bordure);
            border-radius: 4px;
            font-family: inherit;
            font-size: 0.95rem;
            background: #fff;
        }
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: var(--orchidee);
            box-shadow: 0 0 0 2px rgba(183, 68, 184, 0.2);
        }
        .btn-submit {
            height: 44px;
            padding: 0 20px;
            font-family: inherit;
            font-weight: 700;
            background: var(--orchidee);
            color: #fff;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: background 0.15s;
        }
        .btn-submit:hover {
            background: var(--orchidee-fonce);
        }
        .table-container {
            background: #fff;
            border: 1px solid var(--bordure);
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 6px rgba(0,0,0,0.04);
        }
        table.custom-table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
        }
        table.custom-table th {
            background: var(--gris-fond);
            color: var(--texte);
            font-weight: 700;
            font-size: 0.95rem;
            padding: 14px 16px;
            border-bottom: 1px solid var(--bordure);
        }
        table.custom-table td {
            padding: 14px 16px;
            border-bottom: 1px solid var(--bordure);
            font-size: 0.95rem;
            color: var(--texte);
        }
        table.custom-table tr:last-child td {
            border-bottom: none;
        }
        table.custom-table tr:hover {
            background: #fafafc;
        }
        .badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 0.8rem;
            font-weight: 700;
            text-transform: uppercase;
        }
        .badge-effectue {
            background: #e8f5e9;
            color: #2e7d32;
        }
        .badge-attente {
            background: #fff8e1;
            color: #f57f17;
        }
        .badge-echoue {
            background: #ffebee;
            color: #c62828;
        }
        .btn-small {
            padding: 6px 12px;
            font-size: 0.85rem;
            font-weight: 700;
            color: #fff;
            background: #2e7d32;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn-small:hover {
            background: #1b5e20;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="page-container">
        <div class="page-header">
            <h1 class="page-title">Gestion des Paiements</h1>
            <p class="page-subtitle">Enregistrement et suivi des encaissements clients.</p>
        </div>

        <!-- Formulaire d'enregistrement d'un paiement -->
        <div class="form-card">
            <h2>Enregistrer un nouveau paiement</h2>
            <form method="post" action="${pageContext.request.contextPath}/agent/paiements">
                <div class="form-grid">
                    <div class="form-group">
                        <label for="idContrat">Contrat</label>
                        <select id="idContrat" name="idContrat" required>
                            <option value="">-- Sélectionner un contrat --</option>
                            <c:forEach var="c" items="${contrats}">
                                <option value="${c.idContrat}">
                                    ${c.numContrat} (<c:if test="${c.utilisateur != null}">${c.utilisateur.prenom} ${c.utilisateur.nom}</c:if> - ${c.prime} €)
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="montant">Montant (€)</label>
                        <input type="number" step="0.01" min="0.01" id="montant" name="montant" placeholder="ex: 150.00" required>
                    </div>

                    <div class="form-group">
                        <label for="modePaiement">Mode de règlement</label>
                        <select id="modePaiement" name="modePaiement" required>
                            <option value="CARTE">Carte bancaire</option>
                            <option value="VIREMENT">Virement</option>
                            <option value="PRELEVEMENT">Prélèvement</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="datePaiement">Date</label>
                        <input type="date" id="datePaiement" name="datePaiement">
                    </div>

                    <div class="form-group">
                        <label for="statut">Statut</label>
                        <select id="statut" name="statut">
                            <option value="EFFECTUE">Effectué</option>
                            <option value="EN_ATTENTE">En attente</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <button type="submit" class="btn-submit">Valider</button>
                    </div>
                </div>
            </form>
        </div>

        <!-- Tableau des paiements existants -->
        <div class="table-container">
            <table class="custom-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>N° Contrat</th>
                        <th>Client</th>
                        <th>Date</th>
                        <th>Montant</th>
                        <th>Mode</th>
                        <th>Statut</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty paiements}">
                            <c:forEach var="p" items="${paiements}">
                                <tr>
                                    <td>${p.idPaiement}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.contrat != null}">
                                                ${p.contrat.numContrat}
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${p.contrat != null && p.contrat.utilisateur != null}">
                                            ${p.contrat.utilisateur.prenom} ${p.contrat.utilisateur.nom}
                                        </c:if>
                                    </td>
                                    <td>${p.datePaiement}</td>
                                    <td><strong>${p.montant} €</strong></td>
                                    <td>${p.modePaiement}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.statut == 'EFFECTUE'}">
                                                <span class="badge badge-effectue">Effectué</span>
                                            </c:when>
                                            <c:when test="${p.statut == 'EN_ATTENTE'}">
                                                <span class="badge badge-attente">En attente</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-echoue">${p.statut}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${p.statut == 'EN_ATTENTE'}">
                                            <form method="post" action="${pageContext.request.contextPath}/agent/paiements" style="margin: 0;">
                                                <input type="hidden" name="action" value="encaisser">
                                                <input type="hidden" name="idPaiement" value="${p.idPaiement}">
                                                <input type="hidden" name="statut" value="EFFECTUE">
                                                <button type="submit" class="btn-small">Encaisser</button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" style="padding: 30px; text-align: center; color: var(--texte-doux);">
                                    Aucun paiement enregistré pour le moment.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </main>

    <%@ include file="/WEB-INF/footer.jspf" %>
</body>
</html>
