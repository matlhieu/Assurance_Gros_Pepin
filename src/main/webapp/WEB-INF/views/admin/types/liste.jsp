<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestion des Types d'Assurance</title>
</head>
<body>

    <h1>Liste des Types d'Assurance</h1>

    <!-- Lien pour ajouter un nouveau type -->
    <p>
        <a href="${pageContext.request.contextPath}/admin/types?action=form">+ Ajouter un type d'assurance</a>
    </p>

    <!-- Tableau des types -->
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
            <tr>
                <th>ID</th>
                <th>Nom</th>
                <th>Description</th>
                <th>Prix de base (€)</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty types}">
                    <tr>
                        <td colspan="5" style="text-align: center;">Aucun type d'assurance enregistré.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="type" items="${types}">
                        <tr>
                            <td>${type.idTypeAssurance}</td>
                            <td>${type.nom}</td>
                            <td>${type.description}</td>
                            <td>${type.prixBase}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/types?action=form&id=${type.idTypeAssurance}">Modifier</a> |
                                <a href="${pageContext.request.contextPath}/admin/types?action=delete&id=${type.idTypeAssurance}" 
                                   onclick="return confirm('Voulez-vous vraiment supprimer ce type d\'assurance ?');">Supprimer</a>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

    <br>
    <a href="${pageContext.request.contextPath}/index.jsp">Retour à l'accueil</a>

</body>
</html>