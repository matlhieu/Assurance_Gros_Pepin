<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Gestion des Paiements – Agent</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
    <style>
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid var(--bord); }
        th { background-color: var(--bleu); }
        .badge { padding: 4px 8px; border-radius: 4px; font-size: 0.8em; font-weight: bold; color: white; }
        .badge-EN_ATTENTE { background-color: var(--pervenche); }
        .badge-EFFECTUE { background-color: #4CAF50; }
        .badge-ECHOUE { background-color: #F44336; }
        .form-inline { display: flex; gap: 5px; }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>
    
    <main class="accueil" style="grid-template-columns: 1fr; max-width: 1000px; margin: 40px auto;">
        <section class="carte-connexion">
            <div class="carte-chair">
                <h2 class="carte-titre">Gestion des paiements</h2>
                <p class="carte-sous-titre">Liste des échéances en attente de règlement.</p>

                <c:if test="${not empty param.success}">
                    <p class="alerte" style="background: #d4edda; color: #155724; border-left-color: #28a745;">Paiement enregistré avec succès !</p>
                </c:if>
                <c:if test="${not empty param.error}">
                    <p class="alerte">Erreur : ${param.error}</p>
                </c:if>

                <c:if test="${empty paiements}">
                    <p>Aucun paiement en attente.</p>
                </c:if>

                <c:if test="${not empty paiements}">
                    <table>
                        <thead>
                            <tr>
                                <th>ID Paiement</th>
                                <th>Contrat</th>
                                <th>Montant</th>
                                <th>Date d'échéance</th>
                                <th>Statut</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${paiements}">
                                <tr>
                                    <td>#${p.idPaiement}</td>
                                    <td>${p.contrat.numContrat}</td>
                                    <td>${p.montant} €</td>
                                    <td>${p.datePaiement}</td>
                                    <td>
                                        <span class="badge badge-${p.statut}">${p.statut}</span>
                                    </td>
                                    <td>
                                        <form method="POST" action="${pageContext.request.contextPath}/agent/paiements/encaisser" class="form-inline">
                                            <input type="hidden" name="paiementId" value="${p.idPaiement}">
                                            <select name="modePaiement" style="padding: 5px; border-radius: 4px; border: 1px solid var(--bord);">
                                                <option value="CARTE">Carte Bancaire</option>
                                                <option value="VIREMENT">Virement</option>
                                                <option value="PRELEVEMENT">Prélèvement</option>
                                            </select>
                                            <button type="submit" class="btn-principal" style="padding: 5px 10px; font-size: 0.9em;">Encaisser</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
            <div class="carte-ecorce"></div>
        </section>
    </main>
</body>
</html>