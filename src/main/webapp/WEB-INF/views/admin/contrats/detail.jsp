<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Détails du Contrat</title>
</head>
<body>
    <h1>Contrat : ${contrat.numContrat}</h1>

    <p><strong>Type d'assurance :</strong> ${contrat.typeAssurance.nom}</p>
    <p><strong>Période :</strong> du ${contrat.dateDebut} au ${contrat.dateFin}</p>
    <p><strong>Prime :</strong> ${contrat.prime} €</p>
    <p><strong>Statut actuel :</strong> ${contrat.statut}</p>

    <hr>
    <h3>Actions sur le cycle de vie</h3>
    <p>
        <a href="${pageContext.request.contextPath}/admin/contrats/action?action=suspendre&id=${contrat.idContrat}" 
           onclick="return confirm('Suspendre ce contrat ?');">Suspendre</a> |
           
        <a href="${pageContext.request.contextPath}/admin/contrats/action?action=resilier&id=${contrat.idContrat}" 
           onclick="return confirm('Résilier ce contrat ?');">Résilier</a> |
           
        <a href="${pageContext.request.contextPath}/admin/contrats/action?action=renouveler&id=${contrat.idContrat}" 
           onclick="return confirm('Renouveler ce contrat pour 1 an ?');">Renouveler (+1 an)</a>
    </p>

    <br>
    <a href="${pageContext.request.contextPath}/admin/contrats?action=list">Retour à la liste des contrats</a>
</body>
</html>