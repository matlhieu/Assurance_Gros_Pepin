<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${empty garantie ? 'Nouvelle' : 'Modifier'} Garantie</title>
</head>
<body>
    <h1>${empty garantie ? 'Ajouter une garantie' : 'Modifier la garantie'}</h1>

    <form action="${pageContext.request.contextPath}/admin/garanties" method="post">
        
        <c:if test="${not empty garantie}">
            <input type="hidden" name="id" value="${garantie.idGarantie}">
        </c:if>
        
        <input type="hidden" name="action" value="save">

        <div>
            <label for="nom">Nom de la garantie :</label><br>
            <input type="text" id="nom" name="nom" value="${garantie.nom}" required>
        </div>
        <br>
        
        <div>
            <label for="description">Description :</label><br>
            <textarea id="description" name="description" rows="4" cols="40">${garantie.description}</textarea>
        </div>
        <br>
        
        <div>
            <button type="submit">Enregistrer</button>
            <a href="${pageContext.request.contextPath}/admin/garanties?action=list">Annuler</a>
        </div>
    </form>
</body>
</html>