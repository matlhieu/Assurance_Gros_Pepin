<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mes Paiements – Assurance Gros Pépin</title>
    <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png">
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .page-container {
            max-width: 1000px;
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
        .summary-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 20px;
            margin-bottom: 32px;
        }
        .summary-card {
            background: #fff;
            border: 1px solid var(--bordure);
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.04);
        }
        .summary-card h3 {
            margin: 0 0 8px;
            font-size: 0.95rem;
            color: var(--texte-doux);
            font-weight: 600;
        }
        .summary-card .amount {
            font-size: 1.8rem;
            font-weight: 800;
            color: var(--orchidee);
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
            padding: 14px 18px;
            border-bottom: 1px solid var(--bordure);
        }
        table.custom-table td {
            padding: 14px 18px;
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
        .empty-state {
            padding: 40px;
            text-align: center;
            color: var(--texte-doux);
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>

    <main class="page-container">
        <div class="page-header">
            <h1 class="page-title">Mes Paiements</h1>
            <p class="page-subtitle">Consultez l'historique de vos cotisations et vos échéances à venir.</p>
        </div>

        <div class="summary-cards">
            <div class="summary-card">
                <h3>Total réglé</h3>
                <div class="amount">${totalPaye != null ? totalPaye : "0.00"} €</div>
            </div>
            <div class="summary-card">
                <h3>Total en attente</h3>
                <div class="amount" style="color: #f57f17;">${totalEnAttente != null ? totalEnAttente : "0.00"} €</div>
            </div>
        </div>

        <div class="table-container">
            <table class="custom-table">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>N° Contrat</th>
                        <th>Montant</th>
                        <th>Mode de règlement</th>
                        <th>Statut</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty paiements}">
                            <c:forEach var="p" items="${paiements}">
                                <tr>
                                    <td><strong>${p.datePaiement}</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.contrat != null}">
                                                ${p.contrat.numContrat}
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </td>
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
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5" class="empty-state">
                                    Aucun paiement trouvé pour votre compte.
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
