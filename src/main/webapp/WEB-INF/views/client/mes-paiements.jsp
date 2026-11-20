<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mes paiements</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
    <style>
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid var(--bord); }
        th { background-color: var(--bleu); }
        .badge { padding: 4px 8px; border-radius: 4px; font-size: 0.8em; font-weight: bold; color: white; }
        .badge-EN_ATTENTE { background-color: var(--pervenche); }
        .badge-EFFECTUE { background-color: #4CAF50; }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>
    <main class="accueil" style="grid-template-columns: 1fr; max-width: 1000px; margin: 40px auto;">
        <section class="carte-connexion">
            <div class="carte-chair">
                <h2 class="carte-titre">Mes paiements</h2>
                <c:if test="${empty paiements}"><p>Aucun paiement enregistré.</p></c:if>
                <c:if test="${not empty paiements}">
                    <table>
                        <tr><th>Contrat</th><th>Montant</th><th>Date d'échéance</th><th>Statut</th></tr>
                        <c:forEach var="p" items="${paiements}">
                            <tr>
                                <td>${p.contrat.numContrat}</td>
                                <td>${p.montant} €</td>
                                <td>${p.datePaiement}</td>
                                <td><span class="badge badge-${p.statut}">${p.statut}</span></td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:if>
            </div>
        </section>
    </main>
</body>
</html>