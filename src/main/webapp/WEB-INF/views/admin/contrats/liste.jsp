<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestion des Contrats</title>
</head>
<body>
    <h1>Liste des Contrats</h1>
    
    <p>
        <a href="${pageContext.request.contextPath}/admin/contrats?action=form">+ Créer un nouveau contrat</a>
    </p>

    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
            <tr>
                <th>N° Contrat</th>
                <th>Type d'assurance</th>
                <th>Début</th>
                <th>Fin</th>
                <th>Prime</th>
                <th>Statut</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty contrats}">
                    <tr>
                        <td colspan="7" style="text-align: center;">Aucun contrat enregistré.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="c" items="${contrats}">
                        <tr>
                            <td>${c.numContrat}</td>
                            <td>${c.typeAssurance.nom}</td>
                            <td>${c.dateDebut}</td>
                            <td>${c.dateFin}</td>
                            <td>${c.prime} €</td>
                            <td>${c.statut}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/contrats?action=detail&id=${c.idContrat}">Détails</a> |
                                <a href="${pageContext.request.contextPath}/admin/contrats?action=form&id=${c.idContrat}">Modifier</a> |
                                <a href="${pageContext.request.contextPath}/admin/contrats?action=delete&id=${c.idContrat}" 
                                   onclick="return confirm('Voulez-vous vraiment supprimer ce contrat ?');">Supprimer</a>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</body>
</html>