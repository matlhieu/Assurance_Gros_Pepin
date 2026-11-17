<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestion des Garanties</title>
</head>
<body>
    <h1>Liste des Garanties</h1>
    <p>
        <a href="${pageContext.request.contextPath}/admin/garanties?action=form">+ Ajouter une garantie</a>
    </p>

    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
            <tr>
                <th>ID</th>
                <th>Nom</th>
                <th>Description</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty garanties}">
                    <tr>
                        <td colspan="4" style="text-align: center;">Aucune garantie enregistrée.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="g" items="${garanties}">
                        <tr>
                            <td>${g.idGarantie}</td>
                            <td>${g.nom}</td>
                            <td>${g.description}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/garanties?action=form&id=${g.idGarantie}">Modifier</a> |
                                <a href="${pageContext.request.contextPath}/admin/garanties?action=delete&id=${g.idGarantie}" 
                                   onclick="return confirm('Voulez-vous vraiment supprimer cette garantie ?');">Supprimer</a>
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