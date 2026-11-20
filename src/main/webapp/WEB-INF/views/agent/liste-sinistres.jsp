<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Sinistres à traiter – Agent</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
    <style>
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid var(--bord); }
        th { background-color: var(--bleu); }
        .badge { padding: 4px 8px; border-radius: 4px; font-size: 0.8em; font-weight: bold; color: white; }
        .badge-DECLARE { background-color: var(--pervenche); }
        .badge-EN_ANALYSE { background-color: var(--lavande); }
        .badge-ACCEPTE { background-color: #4CAF50; }
        .badge-REFUSE { background-color: #F44336; }
        .badge-INDEMNISE { background-color: #FF9800; }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/header.jspf" %>
    
    <main class="accueil" style="grid-template-columns: 1fr; max-width: 1000px; margin: 40px auto;">
        <section class="carte-connexion">
            <div class="carte-chair">
                <h2 class="carte-titre">Gestion des sinistres</h2>
                <p class="carte-sous-titre">Voici la liste des sinistres en attente de traitement.</p>

                <c:if test="${empty sinistres}">
                    <p>Aucun sinistre à traiter pour le moment.</p>
                </c:if>

                <c:if test="${not empty sinistres}">
                    <table>
                        <thead>
                            <tr>
                                <th>N° Sinistre</th>
                                <th>Date</th>
                                <th>Type</th>
                                <th>Montant</th>
                                <th>Statut</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${sinistres}">
                                <tr>
                                    <td><c:out value="${s.numeroSinistre}" /></td>
                                    <td>${s.dateDeclaration}</td>
                                    <td><c:out value="${s.typeSinistre}" /></td>
                                    <td>${s.montantEstime} €</td>
                                    <td>
                                        <span class="badge badge-${s.statut}">${s.statut}</span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/agent/sinistres/detail?id=${s.idSinistre}" class="btn-contour" style="padding: 5px 10px; font-size: 0.9em;">Traiter</a>
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