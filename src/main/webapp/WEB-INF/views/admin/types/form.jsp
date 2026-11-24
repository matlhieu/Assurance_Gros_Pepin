<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${empty type ? 'Nouveau' : 'Modifier'} Type d'Assurance</title>
</head>
<body>

    <h1>${empty type ? 'Ajouter un type d\'assurance' : 'Modifier le type d\'assurance'}</h1>

    <!-- Formulaire d'ajout / modification -->
    <form action="${pageContext.request.contextPath}/admin/types" method="post">
        
        <!-- On cache l'ID s'il existe (pour la modification) -->
        <c:if test="${not empty type}">
            <input type="hidden" name="id" value="${type.idTypeAssurance}">
        </c:if>
        
        <!-- On indique au servlet qu'on veut sauvegarder -->
        <input type="hidden" name="action" value="save">

        <div>
            <label for="nom">Nom du type :</label><br>
            <input type="text" id="nom" name="nom" value="${type.nom}" required>
        </div>
        <br>
        
        <div>
            <label for="description">Description :</label><br>
            <textarea id="description" name="description" rows="4" cols="40">${type.description}</textarea>
        </div>
        <br>
        
        <div>
            <label for="prixBase">Prix de base (€) :</label><br>
            <input type="number" id="prixBase" name="prixBase" step="0.01" min="0" value="${type.prixBase}" required>
        </div>
        <br>
        
        <div>
            <button type="submit">Enregistrer</button>
            <a href="${pageContext.request.contextPath}/admin/types?action=list">Annuler</a>
        </div>
    </form>

</body>
</html>