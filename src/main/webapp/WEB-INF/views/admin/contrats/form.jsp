<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${empty contrat ? 'Nouveau' : 'Modifier'} Contrat</title>
</head>
<body>
    <h1>${empty contrat ? 'Créer un nouveau contrat' : 'Modifier le contrat'}</h1>

    <form action="${pageContext.request.contextPath}/admin/contrats" method="post">
        
        <!-- ID caché pour la modification -->
        <c:if test="${not empty contrat}">
            <input type="hidden" name="id" value="${contrat.idContrat}">
        </c:if>
        <input type="hidden" name="action" value="save">

        <div>
            <label for="numContrat">Numéro de contrat :</label><br>
            <input type="text" id="numContrat" name="numContrat" value="${contrat.numContrat}" required>
        </div>
        <br>

        <div>
            <label for="idTypeAssurance">Type d'assurance :</label><br>
            <select id="idTypeAssurance" name="idTypeAssurance" required>
                <option value="">-- Sélectionnez un type --</option>
                <c:forEach var="t" items="${typesAssurance}">
                    <option value="${t.idTypeAssurance}" ${contrat.typeAssurance.idTypeAssurance == t.idTypeAssurance ? 'selected' : ''}>
                        ${t.nom}
                    </option>
                </c:forEach>
            </select>
        </div>
        <br>

        <div>
            <label for="dateDebut">Date de début :</label><br>
            <input type="date" id="dateDebut" name="dateDebut" value="${contrat.dateDebut}" required>
        </div>
        <br>

        <div>
            <label for="dateFin">Date de fin :</label><br>
            <input type="date" id="dateFin" name="dateFin" value="${contrat.dateFin}" required>
        </div>
        <br>

        <div>
            <label for="prime">Prime (€) :</label><br>
            <input type="number" id="prime" name="prime" step="0.01" min="0" value="${contrat.prime}" required>
        </div>
        <br>

        <div>
            <label for="statut">Statut :</label><br>
            <select id="statut" name="statut">
                <option value="EN_ATTENTE" ${contrat.statut == 'EN_ATTENTE' ? 'selected' : ''}>En attente</option>
                <option value="ACTIF" ${contrat.statut == 'ACTIF' ? 'selected' : ''}>Actif</option>
                <option value="SUSPENDU" ${contrat.statut == 'SUSPENDU' ? 'selected' : ''}>Suspendu</option>
                <option value="RESILIE" ${contrat.statut == 'RESILIE' ? 'selected' : ''}>Résilié</option>
            </select>
        </div>
        <br>

        <div>
            <button type="submit">Enregistrer</button>
            <a href="${pageContext.request.contextPath}/admin/contrats?action=list">Annuler</a>
        </div>
    </form>
</body>
</html>