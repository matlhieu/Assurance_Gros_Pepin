<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
</head>
<body>
<h2>Statistiques Globales</h2>

<div>
    <p><strong>Chiffre d'affaires encaissé :</strong> ${totalRevenue} €</p>
    <p><strong>Paiements en attente :</strong> ${pendingPayments} €</p>
</div>

<%-- teammates push Contrat & Sinistre
<h3>Contrats par statut</h3>
<ul>
    <c:forEach var="row" items="${contractsByStatus}">
        <li>${row[0]} : ${row[1]}</li>
    </c:forEach>
</ul>
--%>
</body>
</html>